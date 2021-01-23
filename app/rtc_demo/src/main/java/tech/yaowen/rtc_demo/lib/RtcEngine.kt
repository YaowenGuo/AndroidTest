package tech.yaowen.rtc_demo.lib

import android.content.Context
import android.hardware.camera2.CameraMetadata
import org.webrtc.*
import java.util.ArrayList


enum class RtcEngine {
    INSTANCE;

    lateinit var peerConnectionFactory: PeerConnectionFactory

    class Builder {
        var eglBaseContext: EglBase.Context? = null
        var facing: Int = 0
        var threadName: String = "WebRTC"
        fun build(): RtcEngine {
            if (eglBaseContext == null) {
                eglBaseContext = EglBase.create().eglBaseContext
            }
            return INSTANCE
        }
    }

    /**
     * @see CameraMetadata#LENS_FACING_FRONT
     * @see CameraMetadata#LENS_FACING_BACK
     * @see CameraMetadata#LENS_FACING_EXTERNAL
     * 0 any;
     */
    public fun createCameraCapturer(
        context: Context,
        lensFacing: Int = CameraMetadata.LENS_FACING_FRONT
    ): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        var videoCapturer: VideoCapturer? = null
        for (deviceName in deviceNames) {
            when (lensFacing) {
                CameraMetadata.LENS_FACING_FRONT -> {
                    if (enumerator.isFrontFacing(deviceName)) {
                        videoCapturer = enumerator.createCapturer(deviceName, null)
                    }
                }
                CameraMetadata.LENS_FACING_BACK -> {
                    if (enumerator.isBackFacing(deviceName)) {
                        videoCapturer = enumerator.createCapturer(deviceName, null)
                    }
                }

                CameraMetadata.LENS_FACING_EXTERNAL -> {
                    if (!enumerator.isFrontFacing(deviceName) && !enumerator.isBackFacing(deviceName)) {
                        videoCapturer = enumerator.createCapturer(deviceName, null)
                    }
                }
                else -> {
                    videoCapturer = enumerator.createCapturer(deviceName, null)
                }
            }

            if (videoCapturer != null) {
                break
            }
        }
        return videoCapturer
    }

    public fun displayVideo(
        videoTrack: VideoTrack,
        displayView: SurfaceViewRenderer,
        eglBaseContext: EglBase.Context
    ) {
        // display
        displayView.init(eglBaseContext, null)
        displayView.setMirror(true)
        // display in localView
        videoTrack.addSink(displayView)
    }

    // create VideoTrack
    fun createVideoTrack(
        peerConnectionFactory: PeerConnectionFactory,
        id: String,
        videoCapturer: VideoCapturer,
        applicationContext: Context,
        eglBaseContext: EglBase.Context,
        captureThread: String = "CaptureThread"
    ): VideoTrack {

        val videoSource: VideoSource =
            peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        val videoTrack: VideoTrack = peerConnectionFactory.createVideoTrack(id, videoSource)

        val surfaceTextureHelper = SurfaceTextureHelper.create(captureThread, eglBaseContext)

        videoCapturer.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource.capturerObserver
        )
        videoCapturer.startCapture(480, 640, 30)
        return videoTrack
    }

    // create AudioSource
    fun createAudioTrack(peerConnectionFactory: PeerConnectionFactory, id: String): AudioTrack {
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        return peerConnectionFactory.createAudioTrack(id, audioSource)
    }


    public fun createPeerConnection(
        eglBaseContext: EglBase.Context,
        applicationContext: Context
    ): PeerConnectionFactory {
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(applicationContext)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions);

        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)

        peerConnectionFactory =  PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()
        return peerConnectionFactory
    }

    public fun createPeerConnection(applicationContext: Context): PeerConnectionFactory {
        // create PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(applicationContext)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()
        return peerConnectionFactory as PeerConnectionFactory
    }


    public fun connection(
        videoTrack: VideoTrack?,
        sdp: SessionDescription?,
        observer: DspAndIdeObserver
    ): PeerConnection {
        // server 参数传空列表，将创建本地连接。
        val iceServers: List<PeerConnection.IceServer> = ArrayList()
        // 创建 PeerConnection 对象。
        val peerConnection = peerConnectionFactory.createPeerConnection(
            iceServers,
            object : PeerConnection.Observer {
                override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {}
                override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {}
                override fun onIceConnectionReceivingChange(b: Boolean) {}
                override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {}
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    observer.onIceCreate(iceCandidate) // 通过 singling 服务器发送 ice。
                }

                override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {}
                override fun onAddStream(mediaStream: MediaStream) {
                    observer.onAddMediaStream(mediaStream) // 连接之后收到的数据流。
                }

                override fun onRemoveStream(mediaStream: MediaStream) {
                    observer.onRemoveMediaStream(mediaStream)
                }
                override fun onDataChannel(dataChannel: DataChannel) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(
                    rtpReceiver: RtpReceiver,
                    mediaStreams: Array<MediaStream>
                ) {
                }
            })!!

        // 创建 MediaStream 对象。
        val mediaStream = peerConnectionFactory.createLocalMediaStream(if (sdp == null)  "offerMediaStream" else "answerMediaStream")
        mediaStream.addTrack(videoTrack)
        // 添加 MediaStream.
        peerConnection.addStream(mediaStream)
        // 用户创建 Offer 或者 Answer 的回调。
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                // 使用 createOffer 创建的是 offer, 使用 createAnswer 创建的是 answer.
                // 太蠢了，自己的 description 为什么还要设置一次?
                peerConnection.setLocalDescription(this, sdp)
                observer.onDspCreate(sdp)
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(s: String) {}
            override fun onSetFailure(s: String) {}
        }

        // 如果是连接发起者，要创建 offer.
        if (sdp == null) {
            peerConnection.createOffer(sdpObserver, MediaConstraints())
        } else {
            // 如果是响应者，需要先设置 `offer`, 然后才能根据 `offer` 和本地支持的情况，创建 `answer`。
            peerConnection.setRemoteDescription(sdpObserver, sdp)
            // 创建 answer。
            peerConnection.createAnswer(sdpObserver, MediaConstraints())
        }
        return peerConnection;
    }


    interface DspAndIdeObserver {
        fun onDspCreate(sessionDescription: SessionDescription) {};

        fun onIceCreate(iceCandidate: IceCandidate)

        fun onAddMediaStream(mediaStream: MediaStream) {}

        fun onRemoveMediaStream(mediaStream: MediaStream) {}
    }
}