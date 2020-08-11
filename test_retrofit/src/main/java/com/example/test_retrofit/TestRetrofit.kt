package com.example.test_retrofit

import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tools777.ighashtags.model.net.Client
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


fun main() {

    val serverAPI = Client.getServerApi()

    serverAPI.testResponseCode()
        .subscribeBy(
            onError = {
                println(it.message)
            }, onSuccess = {
                println(it)
            }
        )

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