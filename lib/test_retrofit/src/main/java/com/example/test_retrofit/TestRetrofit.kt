package com.example.test_retrofit

import io.reactivex.rxjava3.kotlin.subscribeBy
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.test_retrofit.net.Client
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException


fun main() {

    val serverAPI = Client.getServerApi()

    serverAPI.testResponseCode()
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onError = {
                println(it.message)
            }, onSuccess = {
                print(it.message())
                println(it)
            }
        )

    Thread.sleep(4000)
/*    serverAPI.testRetrofitCall()
        .enqueue(object :Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    println("Success")

                } else {
                    println("Error")
                }
                println("body: ")
                println(response.body())
                println("errorBody: ")
                println(response.errorBody())
                println("raw: ")
                println(response.raw().headers())
            }

        })*/

}


internal class GetExample {
    var client = OkHttpClient()

    @Throws(IOException::class)
    fun run(url: String?) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                }
            })
    }

    companion object {
        @Throws(IOException::class)
        fun test(args: Array<String>) {
            val example = GetExample()
            val response = example.run("https://raw.github.com/square/okhttp/master/README.md")
            println(response)
        }
    }
}