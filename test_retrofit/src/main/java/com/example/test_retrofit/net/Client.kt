package tools777.ighashtags.model.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class Client private constructor() {


    companion object {

        private lateinit var okHttpClient: OkHttpClient
        private lateinit var serverApi: ServerAPI
        private lateinit var sparkHttpClient: OkHttpClient

        fun getServerApi(): ServerAPI {
            serverApi = if (Companion::serverApi.isInitialized) serverApi else {
                okHttpClient = if (Companion::okHttpClient.isInitialized) okHttpClient else {
                    val builder = OkHttpClient.Builder()

                    builder
                        .retryOnConnectionFailure(true)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build()
                }



                Retrofit.Builder()
                    .baseUrl("https:api.github.com/")
                    .client(okHttpClient)
                    .build()
                    .create(ServerAPI::class.java)
            }

            return serverApi
        }
    }
}