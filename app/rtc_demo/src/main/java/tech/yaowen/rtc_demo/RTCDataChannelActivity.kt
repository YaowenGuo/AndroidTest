package tech.yaowen.rtc_demo

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.webrtc.*
import tech.yaowen.rtc_demo.lib.RtcEngine
import java.nio.ByteBuffer
import java.util.*


class RTCDataChannelActivity : BaseActivity() {
    lateinit var peerConnectionFactory: PeerConnectionFactory
    val eglBaseContext = EglBase.create().eglBaseContext
    lateinit var localDataChannel: DataChannel
    lateinit var remoteDataChannel: DataChannel
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    val buffer = DataChannel.Buffer(byteBuffer, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rtc_data_channel_activity)
    }

    override fun onHaveCameraPermission() {
        peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnection(eglBaseContext, this)
        call()
        val sponsorText = findViewById<EditText>(R.id.sponsorSend)
        sponsorText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event!!.action == KeyEvent.ACTION_DOWN) {
                    byteBuffer.clear()
                    byteBuffer.put(sponsorText.text.toString().toByteArray())
                    buffer.data.flip() // 必须提前转变为读取模式。send 是通过 buffer.data.remaining() 获取数据大小的。
                    localDataChannel.send(buffer)
                    return true
                }
                return false
            }
        })

        val responderText = findViewById<EditText>(R.id.responderSend)
        responderText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event!!.action == KeyEvent.ACTION_DOWN) {
                    byteBuffer.clear()
                    byteBuffer.put(responderText.text.toString().toByteArray())
                    buffer.data.flip() // 必须提前转变为读取模式。send 是通过 buffer.data.remaining() 获取数据大小的。
                    remoteDataChannel.send(buffer)
                    return true
                }
                return false
            }
        })

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
                            // 好他妈恶心呀，怎么会有这么难用的 buffer.
                            //  msg?.data?.flip() // 主要是改变 position 的值。默认的读模式，无法使用 remaining().
                            //  因为 position = 0, limit = position 也是 0
                            // remaining = limit - position = 0;
                            // val remaining = msg?.data?.remaining() ?: 0
                            val data = ByteArray(msg?.data?.remaining() ?: 0)
                            msg?.data?.get(data)
                            // val value = data.toString() // 返回的是地址。
                            findViewById<TextView>(R.id.sponsorReceive).text = String(data)
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
                            // val value = data.toString() // 返回的是地址。
                            findViewById<TextView>(R.id.responderReceive).text = String(data)
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

