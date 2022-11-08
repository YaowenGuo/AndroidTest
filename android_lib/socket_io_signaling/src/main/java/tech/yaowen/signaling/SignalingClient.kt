package tech.yaowen.signaling

import android.app.Application
import android.os.Handler
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
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

open class SignalingClient constructor(context: Application) {
    private lateinit var socket: Socket
    private var callback: Callback? = null
    fun setCallback(callback: Callback): SignalingClient {
        this.callback = callback
        return this
    }
    val handler = Handler(context.mainLooper)

    private fun init(context: Application) {

        try {
            // socket = IO.socket(Server.URL);
            // 或者
            // customCertificate
            socket = customCertificate(context)
            socket.connect()
            socket.on("created") { args: Array<Any?>? ->
                threadCurrent("created")
                // 房间创建者收到此回调
                Log.e("webrtc_lim", "created")
                handler.post {
                    callback?.onCreateRoom()
                }
            }

            socket.on("joined") { args: Array<Any?>? ->
                threadCurrent("joined")
                Log.e("webrtc_lim", "joined")
                handler.post {
                    callback?.onJoinedRoom()
                }
            }

            socket.on("join") { args: Array<Any?>? ->
                // 其他用户加入的时候收到此回调
                Log.e("webrtc_lim", "join")
                handler.post {
                    callback?.onPeerJoined()
                }
            }

            socket.on("full") { args: Array<Any?>? ->
                Log.e("webrtc_lim", "room full")
            }

            socket.on("log") { args: Array<Any?>? ->
                if (args == null) return@on
                for (obj in args) {
                    Log.e("webrtc_lim", "log: $obj")
                }
            }
            socket.on("bye") { args: Array<Any> ->
                Log.e("webrtc_lim", "bye")
                handler.post {
                    callback?.onPeerLeave(args[0] as String)
                }
            }
            socket.on("message") { args: Array<Any?> ->
                threadCurrent("message")
                when (val data = args[0]) {
                    is String -> {
                        when (data) {
                            "got user media" -> {
                                handler.post {
                                    callback?.onPeerReady();
                                }
                            }
                        }
                        Log.e("webrtc_lim", "message $data")
                    }
                    is JSONObject -> {
                        Log.e("webrtc_lim", "message $data")
                        when (data.optString("type")) {
                            "offer" -> {
                                handler.post {
                                    callback?.onOfferReceived(data.optString("sdp"))
                                }
                            }
                            "answer" -> {
                                handler.post {
                                    callback?.onAnswerReceived(data.optString("sdp"))
                                }
                            }
                            "candidate" -> {
                                handler.post {
                                    callback?.onIceCandidateReceived(data)
                                }
                            }
                        }
                    }
                    else -> {
                        Log.e("webrtc_lim", "message $args")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun joinRoom(room: String) {
        Log.e("webrtc_lim", "create or join")
        socket.emit("create or join", room)
    }

    fun leave() {
        Log.e("webrtc_lim", "bye")
        socket.emit("bye")
    }

    interface Callback {
        fun onCreateRoom()
        fun onJoinedRoom()
        fun onPeerJoined()
        fun onPeerReady()
        fun onPeerLeave(msg: String?)
        fun onOfferReceived(sd: String)
        fun onAnswerReceived(sd: String)
        fun onIceCandidateReceived(data: JSONObject)
    }

    @Throws(
        URISyntaxException::class,
        CertificateException::class,
        KeyStoreException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    private fun customCertificate(context: Application): Socket {
        val sslParams =
            HttpsUtil.getSslSocketFactory(arrayOf(context.assets.open("burp.pem")), null, null)
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(HttpsUtil.UnSafeTrustManager()), SecureRandom())

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
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


    // 一定要传 object, 传 String 在 js 端无法自动转换成对象。
    fun sendMessage(msg: Any) {
        Log.e("webrtc_lim", "Sending message: $msg")
        socket.emit("message", msg)
    }

    companion object {
        var instance: SignalingClient? = null
        @JvmStatic
        operator fun get(context: Application): SignalingClient {
            if (instance == null) {
                synchronized(SignalingClient::class.java) {
                    if (instance == null) {
                        instance = SignalingClient(context)
                    }
                }
            }
            return instance!!
        }

        @JvmStatic
        open fun threadCurrent(tag: String?) {
            Log.e("RTC-DEMO", "Current Thread: id = " + Thread.currentThread().id + " tag = " + tag)
        }
    }

    init {
        init(context)
    }
}