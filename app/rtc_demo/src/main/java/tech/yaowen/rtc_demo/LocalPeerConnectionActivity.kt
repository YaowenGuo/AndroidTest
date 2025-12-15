package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import org.webrtc.*
import tech.yaowen.rtc_demo.lib.RtcEngine
import tech.yaowen.rtc_demo.ui.VideoScreen


class LocalPeerConnectionActivity : BaseActivity() {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    val eglBaseContext = EglBase.create().eglBaseContext
    private var localView: org.webrtc.SurfaceViewRenderer? = null
    private var remoteView: org.webrtc.SurfaceViewRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(
            ComposeView(this).apply {
                setContent {
                    MaterialTheme {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            VideoScreen(
                                onLocalViewCreated = { view ->
                                    localView = view
                                },
                                onRemoteViewCreated = { view ->
                                    remoteView = view
                                }
                            )
                        }
                    }
                }
            }
        )
    }

    override fun onHaveCameraPermission() {

        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        var localVideoTrack: VideoTrack? = null
        val frontCameraCapture =
            RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        frontCameraCapture?.let {
            val id = "front"
            localVideoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, it, this, eglBaseContext, "FrontCapture"
            )
//            RtcEngine.INSTANCE.displayVideo(
//                localVideoTrack!!,
//                findViewById(R.id.localView),
//                eglBaseContext
//            )
            val localAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)

            call(localVideoTrack!!)
        }
    }

    lateinit var peerConnectionRemote: PeerConnection
    lateinit var peerConnectionLocal: PeerConnection
    val sdpObserver = object : SdpObserver {
        override fun onSetFailure(msg: String?) {
        }

        override fun onSetSuccess() {
        }

        override fun onCreateSuccess(sdp: SessionDescription?) {
        }

        override fun onCreateFailure(msg: String?) {
        }

    }

    private fun call(videoTrack: VideoTrack) {
        peerConnectionLocal = RtcEngine.INSTANCE.connection(null, videoTrack, null, object :
            RtcEngine.DspAndIdeObserver {
            override fun onSdCreate(sdp: SessionDescription) {
                // 通过 Singling 服务器发送 offer。对方接收到后设置。
                answer(videoTrack, sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                peerConnectionRemote.addIceCandidate(iceCandidate)
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    val video = mediaStream.videoTracks[0]
                    localView?.let {
                        RtcEngine.INSTANCE.displayVideo(
                            video,
                            it,
                            eglBaseContext
                        )
                    }
                }
            }
        })
    }


    private fun answer(videoTrack: VideoTrack, sdp: SessionDescription) {
        peerConnectionRemote = RtcEngine.INSTANCE.connection(null, videoTrack, sdp, object :
            RtcEngine.DspAndIdeObserver {
            override fun onSdCreate(sdp: SessionDescription) {
                // 应答方通过 Singling 服务器发送 answer。对方接收到后设置。
                peerConnectionLocal.setRemoteDescription(sdpObserver, sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                peerConnectionLocal.addIceCandidate(iceCandidate)
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    val video = mediaStream.videoTracks[0]
                    remoteView?.let {
                        RtcEngine.INSTANCE.displayVideo(
                            video!!,
                            it,
                            eglBaseContext
                        )
                    }
                }
            }
        })
    }
}
