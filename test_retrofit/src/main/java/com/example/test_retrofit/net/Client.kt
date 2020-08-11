package tools777.ighashtags.model.net


import com.example.test_retrofit.net.ServerAPI
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

class Client private constructor() {


    companion object {
        const val GITHUB = "https:api.github.com/"
        const val LOCAL = "http:127.0.0.1:9991/"
        private lateinit var okHttpClient: OkHttpClient
        private lateinit var serverApi: ServerAPI
        private lateinit var sparkHttpClient: OkHttpClient

        fun getServerApi(): ServerAPI {
            serverApi = if (Companion::serverApi.isInitialized) serverApi else {
                okHttpClient = if (Companion::okHttpClient.isInitialized) okHttpClient else {
                    val builder = OkHttpClient.Builder()

                    builder
                        .retryOnConnectionFailure(true)
                        .authenticator(object :Authenticator {
                            override fun authenticate(route: Route?, response: Response): Request? {
                                val request = Request.Builder().url("authenticator url").build()
                                val response = okHttpClient.newCall(request).execute()
                                val token = "" // response.body(). 获取token.
                                return response.request().newBuilder()
                                    .addHeader("Authorization","Bearer {$token}")
                                    .build()
                            }

                        } )
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build()
                }

                Retrofit.Builder()
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .baseUrl(LOCAL)
                    .client(okHttpClient)
                    .build()
                    .create(ServerAPI::class.java)
            }

            return serverApi
        }
    }
}