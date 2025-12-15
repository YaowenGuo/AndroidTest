package tech.yaowen.rtc_demo

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import org.webrtc.*
import tech.yaowen.rtc_demo.lib.RtcEngine
import tech.yaowen.rtc_demo.ui.RTCDataChannelScreen
import java.nio.ByteBuffer
import java.util.*


class RTCDataChannelActivity : BaseActivity() {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    val eglBaseContext = EglBase.create().eglBaseContext
    lateinit var localDataChannel: DataChannel
    lateinit var remoteDataChannel: DataChannel
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    val buffer = DataChannel.Buffer(byteBuffer, false)
    
    private var sponsorReceiveText = ""
    private var responderReceiveText = ""
    private var sponsorSendText = ""
    private var responderSendText = ""
    private var updateUI: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var sponsorReceive by remember { mutableStateOf("") }
            var responderReceive by remember { mutableStateOf("") }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RTCDataChannelScreen(
                        sponsorReceiveText = sponsorReceive,
                        responderReceiveText = responderReceive,
                        onSponsorTextChange = { text ->
                            sponsorSendText = text
                        },
                        onResponderTextChange = { text ->
                            responderSendText = text
                        },
                        onSponsorSend = {
                            if (sponsorSendText.isNotEmpty()) {
                                byteBuffer.clear()
                                byteBuffer.put(sponsorSendText.toByteArray())
                                buffer.data.flip()
                                localDataChannel.send(buffer)
                                sponsorSendText = ""
                            }
                        },
                        onResponderSend = {
                            if (responderSendText.isNotEmpty()) {
                                byteBuffer.clear()
                                byteBuffer.put(responderSendText.toByteArray())
                                buffer.data.flip()
                                remoteDataChannel.send(buffer)
                                responderSendText = ""
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onHaveCameraPermission() {
        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        call()
    }

    lateinit var peerConnectionLocal: PeerConnection
    lateinit var peerConnectionRemote: PeerConnection
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

    private fun call() {
        peerConnectionLocal = connection(null, object :
            RtcEngine.DspAndIdeObserver {
            override fun onSdCreate(sdp: SessionDescription) {
                // 通过 Singling 服务器发送 offer。对方接收到后设置。
                answer(sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                peerConnectionRemote.addIceCandidate(iceCandidate)
            }

            override fun onAddMediaStream(mediaStream: MediaStream) {
                // 接收数据流
                // Note: This activity doesn't display video, only data channel
            }
        })
    }


    private fun answer(sdp: SessionDescription) {
        peerConnectionRemote = answerConnection(sdp, object :
            RtcEngine.DspAndIdeObserver {
            override fun onSdCreate(sdp: SessionDescription) {
                // 应答方通过 Singling 服务器发送 answer。对方接收到后设置。
                peerConnectionLocal.setRemoteDescription(sdpObserver, sdp)
            }

            override fun onIceCreate(iceCandidate: IceCandidate) {
                // 通过 Singling 服务器发送 ice。对方接收到后设置。
                peerConnectionLocal.addIceCandidate(iceCandidate)


            }
        })
    }

    public fun connection(
        sdp: SessionDescription?,
        observer: RtcEngine.DspAndIdeObserver
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
                }

                override fun onRemoveStream(mediaStream: MediaStream) {
                    observer.onRemoveMediaStream(mediaStream)
                }

                override fun onDataChannel(dataChannel: DataChannel) {
                    Log.e("onDataChannel", "onDataChannel local")
                    dataChannel.registerObserver(object : DataChannel.Observer {
                        override fun onMessage(msg: DataChannel.Buffer?) {
                            val data = ByteArray(msg?.data?.remaining() ?: 0)
                            msg?.data?.get(data)
                            runOnUiThread {
                                sponsorReceiveText = String(data)
                                updateUI?.invoke()
                            }
                        }

                        override fun onBufferedAmountChange(amount: Long) {
                        }

                        override fun onStateChange() {
                            Log.e("onStateChange", "onStateChange: ${remoteDataChannel.state()}")
                        }
                    })
                }

                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(
                    rtpReceiver: RtpReceiver,
                    mediaStreams: Array<MediaStream>
                ) {
                }
            })!!

        // 用户创建 Offer 或者 Answer 的回调。
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                // 使用 createOffer 创建的是 offer, 使用 createAnswer 创建的是 answer.
                // 太蠢了，自己的 description 为什么还要设置一次?
                peerConnection.setLocalDescription(this, sdp)
                observer.onSdCreate(sdp)
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(s: String) {}
            override fun onSetFailure(s: String) {}
        }


        // 如果是连接发起者，要创建 offer.
        if (sdp == null) {
            // 发起端 创建数据通道,必须在发SDP之前
            localDataChannel = peerConnection.createDataChannel("send", DataChannel.Init())
            peerConnection.createOffer(sdpObserver, MediaConstraints())
        } else {
            // 如果是响应者，需要先设置 `offer`, 然后才能根据 `offer` 和本地支持的情况，创建 `answer`。
            peerConnection.setRemoteDescription(sdpObserver, sdp)
            // 创建 answer。
            peerConnection.createAnswer(sdpObserver, MediaConstraints())
        }
        return peerConnection
    }

    public fun answerConnection(
        sdp: SessionDescription?,
        observer: RtcEngine.DspAndIdeObserver
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
                }

                override fun onRemoveStream(mediaStream: MediaStream) {
                    observer.onRemoveMediaStream(mediaStream)
                }

                override fun onDataChannel(dataChannel: DataChannel) {
                    dataChannel.registerObserver(object : DataChannel.Observer {
                        override fun onMessage(msg: DataChannel.Buffer?) {
                            val data = ByteArray(msg?.data?.remaining() ?: 0)
                            msg?.data?.get(data)
                            runOnUiThread {
                                responderReceiveText = String(data)
                                updateUI?.invoke()
                            }
                        }

                        override fun onBufferedAmountChange(amount: Long) {
                        }

                        override fun onStateChange() {
                            Log.e("onStateChange", "onStateChange: ${remoteDataChannel.state()}")
                        }
                    })
                }

                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(
                    rtpReceiver: RtpReceiver,
                    mediaStreams: Array<MediaStream>
                ) {
                }
            })!!

        // 用户创建 Offer 或者 Answer 的回调。
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                // 使用 createOffer 创建的是 offer, 使用 createAnswer 创建的是 answer.
                // 太蠢了，自己的 description 为什么还要设置一次?
                peerConnection.setLocalDescription(this, sdp)
                observer.onSdCreate(sdp)
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(s: String) {}
            override fun onSetFailure(s: String) {}
        }
        // 如果是响应者，需要先设置 `offer`, 然后才能根据 `offer` 和本地支持的情况，创建 `answer`。
        peerConnection.setRemoteDescription(sdpObserver, sdp)
        remoteDataChannel = peerConnection.createDataChannel("remoteSend", DataChannel.Init())
        // 创建 answer。
        peerConnection.createAnswer(sdpObserver, MediaConstraints())
        return peerConnection
    }
}
