package com.example.test_retrofit

import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.test_retrofit.net.Client
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.jvm.Throws


fun main() {

    val serverAPI = Client.getServerApi()

/*    serverAPI.testResponseCode()
//        .subscribeBy(
//            onError = {
//                println("H......" + Thread.currentThread().name)
//                println(it.message)
//            }, onSuccess = {
//                println("--------------")
//                println(Thread.currentThread().getName())
//                println("--------------")
//                print(it.message())
//                println(it)
//            }
//        )
//        .subscribeOn(Schedulers.io())

        .subscribe(object : SingleObserver<Response<Void>> {
            override fun onSubscribe(d: Disposable?) {
                println("Subscribe......" + Thread.currentThread().name)
            }

            override fun onSuccess(t: Response<Void>?) {
                println("Success......" + Thread.currentThread().name)
                t?.code()
            }

            override fun onError(e: Throwable?) {
                println("error......" + Thread.currentThread().name)
                println("error......" + e?.message)
            }

        })*/

    Thread.sleep(10000)
    serverAPI.testRetrofitCall()
        .enqueue(object : Callback<ResponseBody> {
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
                println(response.raw().headers)
            }

        })

}


internal class GetExample {
    var client = OkHttpClient()

    @Throws(IOException::class)
    fun run(url: String) {
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