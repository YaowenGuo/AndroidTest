package com.example.test_retrofit.net

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ServerAPI {

    @GET("users/{user}/repos")
    fun groupList(@Path("user") user: String): Call<ResponseBody>

    @GET("test")
    fun testResponseCode(): Single<Response<Void>>


    @GET("empty.html")
    fun testEmptyBody(): Completable

    @GET("test")
    fun testRetrofitCall(): Call<ResponseBody>
}