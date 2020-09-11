package com.example.test_retrofit.net


import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception
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

                    builder.retryOnConnectionFailure(true)
                        .authenticator(object :Authenticator {
                            override fun authenticate(route: Route?, response: Response): Request? {
                                synchronized(Client::class.java) {
                                    val token = if (getCachedAuth() == response.request.header("Authorization" )) {
                                        val request = Request.Builder().url("authenticator url").build()
                                        val response = okHttpClient.newCall(request).execute()
                                        "从请求结果 response 中拿到" // response.body(). 获取token.
                                    } else {
                                        getCachedAuth()
                                    }

                                    return response.request.newBuilder()
                                        .addHeader("Authorization","Bearer {$token}")
                                        .build()
                                }
                            }
                        } )
                        .addInterceptor {chain ->
                             throw IllegalStateException("Hellp")
                             chain.proceed(chain.request())
                        }
                        .addInterceptor(ResponseStatusInterceptor())
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build()
                }

                Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(ObserveOnMainCallAdapterFactory(Schedulers.newThread()))
//                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .baseUrl(LOCAL)
                    .client(okHttpClient)
                    .build()
                    .create(ServerAPI::class.java)
            }

            return serverApi
        }

        fun getCachedAuth(): String  {
            return "本地存贮的值"
        }
    }
}