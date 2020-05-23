package com.example.test_okhttp

import okhttp3.*
import okhttp3.Request.*
import java.io.IOException

fun main() {
    run()
}


fun run() {
    val client = OkHttpClient()

    val request = Builder()
        .url("http://publicobject.com/helloworld.txt")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                println(response.body!!.string())
            }
        }
    })
}
