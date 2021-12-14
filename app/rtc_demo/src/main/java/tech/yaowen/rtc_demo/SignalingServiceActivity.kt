package tech.yaowen.rtc_demo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import tech.yaowen.signaling.HttpsUtil
import tech.yaowen.signaling.Server
import tech.yaowen.signaling.SignalingClient
import java.io.IOException
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext


class SignalingServiceActivity : AppCompatActivity(), SignalingClient.Callback {
    lateinit var roomView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signaling_service_activity)
        roomView = findViewById(R.id.room)

        SignalingClient[application].setCallback(this)
    }


    fun joinRoom(view: View) {
        val room = roomView.text
        if (room == null || room.isEmpty()) {
            Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show()
        } else {
            SignalingClient[application].joinRoom(room.toString())
        }
    }

    override fun onCreateRoom() {
        Log.e("webrtc_lim", "room created")
    }

    override fun onPeerJoined() {
        Log.e("webrtc_lim", "peer joined")
    }

    override fun onPeerReady() {
        TODO("Not yet implemented")
    }

    override fun onPeerLeave(msg: String?) {
        Log.e("webrtc_lim", "bye $msg")
    }

    override fun onJoinedRoom() {
        Log.e("webrtc_lim", "self joined")
    }


    override fun onOfferReceived(sd: String) {
        Log.e("webrtc_lim", "offer: $sd")
    }


    override fun onAnswerReceived(sd: String) {
        Log.e("webrtc_lim", "answer: $sd")
    }

    override fun onIceCandidateReceived(data: JSONObject) {
        Log.e("webrtc_lim", "Ice: $data")
    }



    private fun customOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        try {
            val sslContext: SSLContext = SSLContext.getInstance("SSL")

            // TODO 自签名证书认证有问题，查一下无法验证的问题。现在是不验证，全部通过。
//            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
//            val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//            keyStore.load(null)
//            val certificateAlias = 0.toString()
//
//            keyStore.setCertificateEntry(
//                certificateAlias,
//                certificateFactory.generateCertificate(assets.open("burp.pem"))
//            )
//            val trustManagerFactory: TrustManagerFactory =
//                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//            trustManagerFactory.init(keyStore)
//            sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())

            sslContext.init(null, arrayOf(HttpsUtil.UnSafeTrustManager()), SecureRandom())

            builder.sslSocketFactory(sslContext.socketFactory, HttpsUtil.UnSafeTrustManager())
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return builder.build()
    }

    private fun testCertificate() {
        val request: Request = Request.Builder()
            .url(Server.URL)
            .build()
        customOkHttpClient().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("webrtc", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.e("webrtc", response.message)
                }

            })
    }

}