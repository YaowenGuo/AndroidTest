package com.example.test_retrofit

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

    println("hello")

    val serverAPI = Client.getServerApi()

    serverAPI.groupList("YaowenGuo")
        .enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println(response.body())
            }
        })

}


internal class GetExample {
    var client = OkHttpClient()

    @Throws(IOException::class)
    fun run(url: String?) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue(object: okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                }
            })
    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val example = GetExample()
            val response =
                example.run("https://raw.github.com/square/okhttp/master/README.md")
            println(response)
        }
    }
}