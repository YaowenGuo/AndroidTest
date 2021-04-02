package tech.yaowen.rtc_demo

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import tech.yaowen.rtc_demo.base.Server
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
    private var socket: Socket? = null
    private val room = "my_room"
    private var callback: Callback? = null
    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    private fun init(context: Context) {
        try {
            // socket = IO.socket(Server.URL);
            // 或者
            // customCertificate
            socket = customCertificate(context)
            socket!!.connect()
            socket!!.on("created") { args: Array<Any?>? ->
                callback!!.onCreateRoom()
            }
            socket!!.on("full") { args: Array<Any?>? ->
                Log.e("webrtc_albert", "room full")
            }
            socket!!.on("join") { args: Array<Any?>? ->
                callback!!.onPeerJoined()
            }
            socket!!.on("joined") { args: Array<Any?>? ->
                callback!!.onSelfJoined()
            }
            socket!!.on("log") { args: Array<Any?>? ->
                Log.e("webrtc_albert", "log call " + args.toString())
            }
            socket!!.on("bye") { args: Array<Any> ->
                callback!!.onPeerLeave(args[0] as String)
            }
            socket!!.on("message") { args: Array<Any?> ->
                Log.e("webrtc_albert", "message $args")
                val data = args[0]
                if (data is String) {
                } else if (data is JSONObject) {
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
        socket!!.emit("create or join", room)
    }

    fun sendIceCandidate(iceCandidate: IceCandidate) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendSessionDescription(sdp: SessionDescription) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    interface Callback {
        fun onCreateRoom()
        fun onPeerJoined()
        fun onSelfJoined()
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
    private fun customCertificate(context: Context): Socket? {
//        val sslParams = HttpsUtil.getSslSocketFactory(arrayOf(context.assets.open("burp.pem")), null, null)
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(HttpsUtil.UnSafeTrustManager()), SecureRandom())

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier { s: String?, sslSession: SSLSession? -> true })
//            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
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