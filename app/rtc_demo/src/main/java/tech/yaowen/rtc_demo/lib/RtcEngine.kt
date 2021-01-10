package tech.yaowen.rtc_demo.lib

import android.content.Context
import android.hardware.camera2.CameraMetadata
import org.webrtc.*


enum class RtcEngine {
    INSTANCE;
    var peerConnection: PeerConnectionFactory? = null

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

        val videoSource: VideoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
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


    public fun createPeerConnection(eglBaseContext: EglBase.Context, applicationContext: Context): PeerConnectionFactory {
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(applicationContext)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions);

        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)

        return PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()
    }

    public fun createPeerConnection(applicationContext: Context): PeerConnectionFactory {
        // create PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(applicationContext)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        val peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()
        return peerConnectionFactory

    }


    public fun createCamera(
        applicationContext: Context,
        peerConnectionFactory: PeerConnectionFactory,
        videoCapturer: VideoCapturer,
        displayView: SurfaceViewRenderer
    ) {
        val eglBaseContext = EglBase.create().eglBaseContext

        val surfaceTextureHelper = SurfaceTextureHelper
            .create("CaptureThread", eglBaseContext)

        // create VideoCapturer
        val videoSource: VideoSource = peerConnectionFactory
            .createVideoSource(videoCapturer.isScreencast)

        videoCapturer.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource.capturerObserver
        )

        videoCapturer.startCapture(480, 640, 30)

        // create VideoTrack
        val videoTrack: VideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)


        // display in localView
        displayView.setMirror(true)
        displayView.init(eglBaseContext, null)
        videoTrack.addSink(displayView);

        val remoteSurfaceTextureHelper =
            SurfaceTextureHelper.create("RemoteCaptureThread", eglBaseContext)

        // create VideoCapturer
//        val remoteVideoCapturer: VideoCapturer? = createCameraCapturer(applicationContext)
//        val remoteVideoSource: VideoSource =
//            peerConnectionFactory.createVideoSource(remoteVideoCapturer.isScreencast)
//        remoteVideoCapturer.initialize(
//            remoteSurfaceTextureHelper,
//            applicationContext,
//            remoteVideoSource.getCapturerObserver()
//        )
//        remoteVideoCapturer.startCapture(480, 640, 30)
    }

    public fun call(
        peerConnectionFactory: PeerConnectionFactory,
        videoTrack: VideoTrack,
        remoteVideoTrack: VideoTrack
    ) {
        val mediaStreamLocal = peerConnectionFactory.createLocalMediaStream("mediaStreamLocal")
        mediaStreamLocal.addTrack(videoTrack)

        val mediaStreamRemote = peerConnectionFactory.createLocalMediaStream("mediaStreamRemote")
        mediaStreamRemote.addTrack(remoteVideoTrack)

//        call(mediaStreamLocal, mediaStreamRemote)
    }
}