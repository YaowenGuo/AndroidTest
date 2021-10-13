package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import tech.yaowen.rtc_demo.base.log
import tech.yaowen.rtc_demo.lib.RtcEngine
import tech.yaowen.signaling.SignalingClient
import tech.yaowen.signaling.data.SessionDescriptionMsg

/**
 * 1. 连接服务器，获取自己是发起者还是应答者
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
    private var isInitiator = false
    private var videoCapturer: VideoCapturer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_peer_connection_activity)
    }

    override fun onHaveCameraPermission() {
        joinRoom()
    }

    private fun joinRoom() {
        JoinRoomDialog(this, JoinRoomDialog.OnSubmitListener {
            SignalingClient[application]
                .setCallback(this)
                .join(it)

        }).show()
    }

    lateinit var peerConnection: PeerConnection


    private fun captureVideoAndVideo() {
        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        videoCapturer = RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        if (videoCapturer != null) {
            val id = "front"
            videoTrack = RtcEngine.INSTANCE.createVideoTrack(
                peerConnectionFactory, id, videoCapturer!!, this, eglBaseContext, "FrontCapture"
            )

            val localAudioTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, id)
            runOnUiThread {
                RtcEngine.INSTANCE.displayVideo(
                    videoTrack!!,
                    findViewById(R.id.localView),
                    eglBaseContext
                )
            }
            if (videoTrack != null) {
                SignalingClient[application]
                    .sendMessage("got user media")
            } else {
                hint("创建视频轨失败")
            }
        } else {
            hint("获取摄像头失败")
        }
    }


    override fun onCreateRoom() {
        joined = true
        isInitiator = true
        captureVideoAndVideo()
    }

    override fun onJoinedRoom() {
        isInitiator = false
        joined = true
        captureVideoAndVideo()
    }

    override fun onPeerReady() {
        // 接收方已经获取到音/视频，可以建立连接了。
        if (isInitiator && videoTrack != null) {
            call(videoTrack!!)
        }
    }


    override fun onPeerJoined() {

    }

    override fun onPeerLeave(msg: String?) {

    }

    override fun onOfferReceived(data: JSONObject?) {
        answer(
            videoTrack!!,
            SessionDescription(SessionDescription.Type.OFFER, data!!.optString("sdp"))
        )
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
            override fun onSdCreate(sdp: SessionDescription) {
                sendSd(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                sendIce(iceCandidate)
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

            override fun onRemoveMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                runOnUiThread {
                    // TODO 如何知道移除的哪一个？
                }
            }
        })
    }

    private fun answer(videoTrack: VideoTrack, sdp: SessionDescription) {
        log("Start to answer")
        peerConnection = RtcEngine.INSTANCE.connection(videoTrack, sdp, object :
            RtcEngine.DspAndIdeObserver {
            override fun onSdCreate(sdp: SessionDescription) {
                sendSd(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                sendIce(iceCandidate)
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
            SignalingClient[application].leave()
        }
        videoCapturer?.stopCapture()
        super.onDestroy()
    }

    private fun hint(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        Log.e("webrtc_albert", str)
    }

    fun sendIce(iceCandidate: IceCandidate) {
        // 通过 Singling 服务器发送 ice。对方接收到后设置。
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            Log.d("Sending ice", jo.toString())
            SignalingClient[application].sendMessage(jo);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendSd(sdp: SessionDescription) {
        // 通过 Singling 服务器发送 offer。对方接收到后设置。
        log("Send offer")
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            SignalingClient[application].sendMessage(jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}