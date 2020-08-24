package com.example.test_retrofit.net

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import sun.security.provider.certpath.OCSPResponse


interface ServerAPI {

    @GET("users/{user}/repos")
    fun groupList(@Path("user") user: String): Call<ResponseBody>

    @GET("test")
    fun testResponseCode(): Single<Response<Void>>


    @GET("empty.html")
    fun testEmptyBody(): Completable

    @GET("test")
    fun testRetrofitCall(): Call<ResponseBody>

    @Headers(ResponseStatusInterceptor.CHECK_HTTP_RESPONSE_CODE)
    @GET("code_1.json")
    fun testCodeInterceptor(): Single<BaseRsp<Void>>

    @Headers(ResponseStatusInterceptor.CHECK_HTTP_RESPONSE_CODE)
    @GET("code_3.json")
    fun testCode3Interceptor(): Single<BaseRsp<Void>>

    @Headers(ResponseStatusInterceptor.CHECK_HTTP_RESPONSE_CODE)
    @GET("no_code.json")
    fun testCodeNoIntercept(): Single<BaseRsp<Void>>


    @Headers(ResponseStatusInterceptor.CHECK_HTTP_RESPONSE_CODE)
    @GET("not_exit_file")
    fun testRetrofitErrorCode(): Call<String>

}