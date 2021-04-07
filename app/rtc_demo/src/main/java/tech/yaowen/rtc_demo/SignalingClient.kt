package tech.yaowen.rtc_demo

import android.content.Context
import android.util.Log
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.thread.EventThread
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import tech.yaowen.rtc_demo.base.Server
import tech.yaowen.rtc_demo.base.log
import tech.yaowen.rtc_demo.lib.HttpsUtil
import java.io.IOException
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import kotlin.jvm.Throws

class SignalingClient private constructor(context: Context) {
    private lateinit var socket: Socket
    private var callback: Callback? = null
    fun setCallback(callback: Callback?): SignalingClient {
        this.callback = callback
        return this
    }

    private fun init(context: Context) {
        try {
            // socket = IO.socket(Server.URL);
            // 或者
            // customCertificate
            socket = customCertificate(context)
            socket.connect()
            socket.on("created") { args: Array<Any?>? ->
                // 房间创建者收到此回调
                Log.e("webrtc_albert", "created")
                callback!!.onCreateRoom()
            }

            socket.on("joined") { args: Array<Any?>? ->
                Log.e("webrtc_albert", "joined")
                callback!!.onJoinedRoom()
            }

            socket.on("join") { args: Array<Any?>? ->
                // 其他用户加入的时候收到此回调
                Log.e("webrtc_albert", "join")
                callback!!.onPeerJoined()
            }

            socket.on("full") { args: Array<Any?>? ->
                Log.e("webrtc_albert", "room full")
            }

            socket.on("log") { args: Array<Any?>? ->
                if (args == null) return@on
                for (obj in args) {
                    Log.e("webrtc_albert", "log: $obj")
                }
            }
            socket.on("bye") { args: Array<Any> ->
                Log.e("webrtc_albert", "bye")
                callback!!.onPeerLeave(args[0] as String)
            }
            socket.on("message") { args: Array<Any?> ->
                when (val data = args[0]) {
                    is String -> {
                        Log.e("webrtc_albert", "message $data")
                    }
                    is JSONObject -> {
                        Log.e("webrtc_albert", "message $data")
                        when (data.optString("type")) {
                            "offer" -> {
                                callback!!.onOfferReceived(data)
                            }
                            "answer" -> {
                                callback!!.onAnswerReceived(data)
                            }
                            "candidate" -> {
                                callback!!.onIceCandidateReceived(data)
                            }
                        }
                    }
                    else -> {
                        Log.e("webrtc_albert", "message $args")
                    }
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun join(room: String) {
        Log.e("webrtc_albert", "create or join")
        socket.emit("create or join", room)
    }

    fun leave() {
        Log.e("webrtc_albert", "bye")
        socket.emit("bye")
    }

    fun sendIceCandidate(iceCandidate: IceCandidate) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            socket.emit("message", jo)
            log("Sending ice: $jo" )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendSessionDescription(sdp: SessionDescription) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            sendMessage(jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    interface Callback {
        fun onCreateRoom()
        fun onJoinedRoom()
        fun onPeerJoined()
        fun onPeerLeave(msg: String?)
        fun onOfferReceived(data: JSONObject?)
        fun onAnswerReceived(data: JSONObject?)
        fun onIceCandidateReceived(data: JSONObject?)
    }

    @Throws(
        URISyntaxException::class,
        CertificateException::class,
        KeyStoreException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    private fun customCertificate(context: Context): Socket {
        val sslParams =
            HttpsUtil.getSslSocketFactory(arrayOf(context.assets.open("burp.pem")), null, null)
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(HttpsUtil.UnSafeTrustManager()), SecureRandom())

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier { s: String?, sslSession: SSLSession? -> true })
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .sslSocketFactory(sslContext.socketFactory, HttpsUtil.UnSafeTrustManager())
            .addInterceptor(HttpLoggingInterceptor())
            .build()


        // set as an option
        val opts = IO.Options()
        opts.callFactory = okHttpClient
        opts.webSocketFactory = okHttpClient
        socket = IO.socket(Server.URL, opts)
        return socket
    }


    fun sendMessage(msg: Any) {
        Log.e("webrtc_albert", "Sending message: $msg")
        socket.emit("message", msg)
    }

    companion object {
        private var instance: SignalingClient? = null
        operator fun get(context: Context): SignalingClient {
            if (instance == null) {
                synchronized(SignalingClient::class.java) {
                    if (instance == null) {
                        instance = SignalingClient(context)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        init(context)
    }
}