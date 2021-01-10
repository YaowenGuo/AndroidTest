package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import org.webrtc.*
import org.webrtc.PeerConnection.*
import tech.yaowen.rtc_demo.lib.RtcEngine
import java.util.*


class LocalPeerConnectionActivity : BaseActivity() {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_peer_connection_activity)
    }

    override fun onHaveCameraPermission() {
        val eglBaseContext = EglBase.create().eglBaseContext
        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        var localVideoTrack:VideoTrack? = null
        val frontCameraCapture =
            RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        frontCameraCapture?.let {
            val id = "front"
            localVideoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, it, this, eglBaseContext, "FrontCapture"
            )
            val localAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)
//            RtcEngine.INSTANCE.displayVideo(
//                localVideoTrack!!,
//                findViewById(R.id.localView),
//                eglBaseContext
//            )


            // 建立
            val mediaStreamLocal = peerConnectionFactory.createLocalMediaStream("mediaStreamLocal")
            mediaStreamLocal.addTrack(localVideoTrack)
            mediaStreamLocal.addTrack(localAudioTrack)

        }

        val backCameraCapture =
            RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_BACK)

        var mediaStreamRemote: MediaStream? = null
        backCameraCapture?.let {
            val id = "back"
            val remoteVideoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, it, this, eglBaseContext, "BackCapture"
            )
            val remoteAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)
//            RtcEngine.INSTANCE.displayVideo(
//                remoteVideoTrack,
//                findViewById(R.id.remoteView),
//                eglBaseContext
//            )

            mediaStreamRemote = peerConnectionFactory.createLocalMediaStream("mediaStreamRemote");
            mediaStreamRemote?.addTrack(remoteVideoTrack)
            mediaStreamRemote?.addTrack(remoteAudioTrack)
        }

        if (localVideoTrack != null && mediaStreamRemote != null) {
            call(
                localVideoTrack!!,
                mediaStreamRemote!!,
                findViewById(R.id.localView),
                findViewById(R.id.remoteView)
            )
        }
    }

    lateinit var peerConnectionRemote: PeerConnection
    lateinit var peerConnectionLocal: PeerConnection

    private fun call(
        videoTrack: VideoTrack,
        remoteMediaStream: MediaStream,
        view: SurfaceViewRenderer, remoteView: SurfaceViewRenderer
    ) {
        val iceServers: List<IceServer> = ArrayList()
        peerConnectionLocal = peerConnectionFactory.createPeerConnection(
            iceServers,
            object : PeerConnection.Observer {
                override fun onSignalingChange(signalingState: SignalingState) {}
                override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {}
                override fun onIceConnectionReceivingChange(b: Boolean) {}
                override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {}
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    peerConnectionRemote.addIceCandidate(iceCandidate)
                }

                override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {}
                override fun onAddStream(mediaStream: MediaStream) {
                    val remoteVideoTrack = mediaStream.videoTracks[0]
                    runOnUiThread { remoteVideoTrack.addSink(view) }
                }

                override fun onRemoveStream(mediaStream: MediaStream) {}
                override fun onDataChannel(dataChannel: DataChannel) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(
                    rtpReceiver: RtpReceiver,
                    mediaStreams: Array<MediaStream>
                ) {
                }
            })!!
        peerConnectionRemote = peerConnectionFactory.createPeerConnection(
            iceServers,
            object : PeerConnection.Observer {
                override fun onSignalingChange(signalingState: SignalingState) {}
                override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {}
                override fun onIceConnectionReceivingChange(b: Boolean) {}
                override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {}
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    peerConnectionLocal.addIceCandidate(iceCandidate)
                }

                override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {}
                override fun onAddStream(mediaStream: MediaStream) {
                    val localVideoTrack = mediaStream.videoTracks[0]
                    runOnUiThread { localVideoTrack.addSink(remoteView) }
                }

                override fun onRemoveStream(mediaStream: MediaStream) {}
                override fun onDataChannel(dataChannel: DataChannel) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(
                    rtpReceiver: RtpReceiver,
                    mediaStreams: Array<MediaStream>
                ) {
                }
            })!!
        val mediaStreamLocal = peerConnectionFactory.createLocalMediaStream("mediaStreamLocal")
        mediaStreamLocal.addTrack(videoTrack)
        peerConnectionLocal.addStream(mediaStreamLocal)
        if (localSdp == null) {
            localSdp = object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    peerConnectionLocal.setLocalDescription(localSdp, sessionDescription)
                    answer(remoteMediaStream, sessionDescription)
                }

                override fun onSetSuccess() {}
                override fun onCreateFailure(s: String) {}
                override fun onSetFailure(s: String) {}
            }
        }
        peerConnectionLocal.createOffer(localSdp, MediaConstraints())
    }

    var localSdp: SdpObserver? = null

    fun receiveAnswer(sessionDescription: SessionDescription?) {
        peerConnectionLocal.setRemoteDescription(localSdp, sessionDescription)
    }

    var remoteSdp: SdpObserver? = null

    fun answer(
        remoteMediaStream: MediaStream?,
        sessionDescription: SessionDescription?
    ) {
        peerConnectionRemote.addStream(remoteMediaStream)
        if (remoteSdp == null) {
            remoteSdp = object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    peerConnectionRemote.setLocalDescription(remoteSdp, sessionDescription)
                    receiveAnswer(sessionDescription)
                }

                override fun onSetSuccess() {}
                override fun onCreateFailure(s: String) {}
                override fun onSetFailure(s: String) {}
            }
        }
        peerConnectionRemote.setRemoteDescription(remoteSdp, sessionDescription)
        peerConnectionRemote.createAnswer(remoteSdp, MediaConstraints())
    }

}

