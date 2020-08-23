package com.example.test_retrofit

import com.example.test_retrofit.net.Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun main() {
    Client.getServerApi()
        .testRetrofitErrorCode()
        .enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                println(t.message)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                println(response.body())
            }

        })

    Thread.sleep(20000)
}