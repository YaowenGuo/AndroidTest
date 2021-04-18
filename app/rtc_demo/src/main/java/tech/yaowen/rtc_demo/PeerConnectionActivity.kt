package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import org.webrtc.*
import tech.yaowen.rtc_demo.base.log
import tech.yaowen.rtc_demo.lib.RtcEngine

/**
 * 1. 连接服务器，获取自己是发起者还是应答着
 * 2. 创建本地视频捕获。
 * 3. 如果是发起者，创建 offer 并发送。如果是接收者，接收 offer 并创建 answer 发送。
 * 4. 设置接收到的 offer/answer.
 * 6. 创建 ICE 信息。
 */

class PeerConnectionActivity : BaseActivity(), SignalingClient.Callback {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    val eglBaseContext = EglBase.create().eglBaseContext
    var videoTrack: VideoTrack? = null
    private var joined = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_peer_connection_activity)
    }

    override fun onHaveCameraPermission() {
        joinRoom()
    }

    private fun joinRoom() {
        JoinRoomDialog(this, JoinRoomDialog.OnSubmitListener {
            SignalingClient[this]
                .setCallback(this)
                .join(it)

        }).show()
    }

    lateinit var peerConnection: PeerConnection


    private fun captureVideoAndVideo() {
        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        val frontCameraCapture = RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        if (frontCameraCapture != null) {
            val id = "front"
            videoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, frontCameraCapture, this, eglBaseContext, "FrontCapture"
            )

            val localAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)
            runOnUiThread {
                RtcEngine.INSTANCE.displayVideo(videoTrack!!, findViewById(R.id.localView), eglBaseContext)
            }
        } else {
            hint("获取摄像头失败")
        }
    }


    override fun onCreateRoom() {
        joined = true
        captureVideoAndVideo()
        if (videoTrack != null) {
            call(videoTrack!!)
        } else {
            hint("创建视频轨失败")
        }
    }

    override fun onJoinedRoom() {
        joined = true
        captureVideoAndVideo()
        if (videoTrack != null) {
            SignalingClient[this]
                .sendMessage("got user media")
        } else {
            hint("创建视频轨失败")
        }
    }


    override fun onPeerJoined() {

    }

    override fun onPeerLeave(msg: String?) {

    }

    override fun onOfferReceived(data: JSONObject?) {
        answer(videoTrack!!, SessionDescription(SessionDescription.Type.OFFER, data!!.optString("sdp")))
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


    private fun call(videoTrack: VideoTrack) {
        log("Start to call")
        peerConnection = RtcEngine.INSTANCE.connection(videoTrack, null, object :
            RtcEngine.DspAndIdeObserver {
            override fun onDspCreate(sdp: SessionDescription) {
                // 通过 Singling 服务器发送 offer。对方接收到后设置。
                log("Send offer")
                SignalingClient[this@PeerConnectionActivity].sendSessionDescription(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                SignalingClient[this@PeerConnectionActivity].sendIceCandidate(iceCandidate);
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    val video = mediaStream.videoTracks[0]
                    RtcEngine.INSTANCE.displayVideo(
                        video,
                        findViewById(R.id.remoteView),
                        eglBaseContext
                    )
                }
            }
        })
    }

    private fun answer(videoTrack: VideoTrack, sdp: SessionDescription) {
        log("Start to answer")
        peerConnection = RtcEngine.INSTANCE.connection(videoTrack, sdp, object :
            RtcEngine.DspAndIdeObserver {
            override fun onDspCreate(sdp: SessionDescription) {
                // 应答方通过 Singling 服务器发送 answer。对方接收到后设置。
                log("Send answer")
                SignalingClient[this@PeerConnectionActivity].sendSessionDescription(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                log("Send ice")
                SignalingClient[this@PeerConnectionActivity].sendIceCandidate(iceCandidate);
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                log("receive media stream")
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

    override fun onDestroy() {
        if (joined) {
            SignalingClient[this].leave()
        }
        super.onDestroy()
    }

    private fun hint(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        Log.e("webrtc_albert", str)
    }
}