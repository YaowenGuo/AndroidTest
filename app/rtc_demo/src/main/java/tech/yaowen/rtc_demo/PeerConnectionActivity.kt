package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import tech.yaowen.rtc_demo.lib.RtcEngine


class PeerConnectionActivity : BaseActivity(), SignalingClient.Callback {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    val eglBaseContext = EglBase.create().eglBaseContext
    var localVideoTrack: VideoTrack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_peer_connection_activity)
    }

    override fun onHaveCameraPermission() {

        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)

        val frontCameraCapture =
            RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        frontCameraCapture?.let {
            val id = "front"
            localVideoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, it, this, eglBaseContext, "FrontCapture"
            )

            val localAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)

            SignalingClient.get().setCallback(this)
//            call(localVideoTrack!!)

        }
    }

    fun connect() {
        val iceServers: MutableList<IceServer> = ArrayList()
        iceServers.add(
            IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
    }

    lateinit var peerConnectionRemote: PeerConnection
    lateinit var peerConnection: PeerConnection
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
        peerConnection = RtcEngine.INSTANCE.connection(videoTrack, null, object :
            RtcEngine.DspAndIdeObserver {
            override fun onDspCreate(sdp: SessionDescription) {
                // 通过 Singling 服务器发送 offer。对方接收到后设置。
//                answer(videoTrack, sdp)
                SignalingClient.get().sendSessionDescription(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                peerConnectionRemote.addIceCandidate(iceCandidate)
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    val video = mediaStream.videoTracks[0]
                    RtcEngine.INSTANCE.displayVideo(
                        video,
                        findViewById(R.id.localView),
                        eglBaseContext
                    )
                }
            }
        })
    }


    private fun answer(videoTrack: VideoTrack, sdp: SessionDescription) {
        peerConnectionRemote = RtcEngine.INSTANCE.connection(videoTrack, sdp, object :
            RtcEngine.DspAndIdeObserver {
            override fun onDspCreate(sdp: SessionDescription) {
                // 应答方通过 Singling 服务器发送 answer。对方接收到后设置。
                SignalingClient.get().sendSessionDescription(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                SignalingClient.get().sendIceCandidate(iceCandidate);
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    val video = mediaStream.videoTracks[0]
                    RtcEngine.INSTANCE.displayVideo(
                        video!!,
                        findViewById(R.id.remoteView),
                        eglBaseContext
                    )
                }
            }
        })
    }

    override fun onCreateRoom() {
    }

    override fun onPeerJoined() {
    }

    override fun onPeerLeave(msg: String?) {
    }

    override fun onSelfJoined() {
        call(localVideoTrack!!)
    }



    override fun onOfferReceived(data: JSONObject?) {
        runOnUiThread {
            answer(localVideoTrack!!, SessionDescription(SessionDescription.Type.OFFER, data!!.optString("sdp")))
        }
    }


    override fun onAnswerReceived(data: JSONObject?) {
        peerConnection.setRemoteDescription(
            object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {}
            override fun onSetSuccess() {}
            override fun onCreateFailure(s: String) {}
            override fun onSetFailure(s: String) {}
        },
            SessionDescription(SessionDescription.Type.ANSWER, data!!.optString("sdp"))
        )
    }

    override fun onIceCandidateReceived(data: JSONObject?) {
        peerConnection.addIceCandidate(
            IceCandidate(
                data!!.optString("id"),
                data.optInt("label"),
                data.optString("candidate")
            )
        )
    }
}