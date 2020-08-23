package com.example.test_retrofit

import com.example.test_retrofit.net.BaseRsp
import com.example.test_retrofit.net.Client
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

fun main() {
/*    Client.getServerApi()
        .testCodeInterceptor()
        .subscribe(object : SingleObserver<BaseRsp<Void>> {
            override fun onSuccess(t: BaseRsp<Void>?) {
                println(t)
            }

            override fun onSubscribe(d: Disposable?) {
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }

        })*/

/*    Client.getServerApi()
        .testCode3Interceptor()
        .subscribe(object : SingleObserver<BaseRsp<Void>> {
            override fun onSuccess(t: BaseRsp<Void>?) {
                println(t)
            }

            override fun onSubscribe(d: Disposable?) {
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }

        })*/


    Client.getServerApi()
        .testCodeNoIntercept()
        .subscribe(object : SingleObserver<BaseRsp<Void>> {
            override fun onSuccess(t: BaseRsp<Void>?) {
                println(t)
            }

            override fun onSubscribe(d: Disposable?) {
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }

        })


    Thread.sleep(200000)
}