package com.example.test_retrofit

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.http.POST
import java.lang.Exception
import kotlin.Throws

class RetrofitBug {

    val server = MockWebServer()

    interface Service {
        @POST("/path")
        fun foo(): Completable
    }

    // exchange this with : IOException() and the test succeeds
    class SomeException : IllegalStateException()

    class SomeInterceptor : Interceptor {
        @Throws(SomeException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            if (true) {
                throw SomeException()
            }
            return chain.proceed(chain.request())
        }
    }

    fun test() {
        val okhttp = okhttp3.OkHttpClient.Builder()
            .addInterceptor(SomeInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okhttp)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            // Or
            // .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .baseUrl(server.url("/"))
            .build()

        val example = retrofit.create(Service::class.java)

        example.foo()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable?) {
                    println("onSubscribe")

                }

                override fun onError(e: Throwable?) {
                    println("onError")
                }

            })
//            .test()
//            .await() // stalls indefinitely
//            .assertError(SomeException::class.java)
    }
}

fun main() {
    val test = RetrofitBug()
    test.test()
}