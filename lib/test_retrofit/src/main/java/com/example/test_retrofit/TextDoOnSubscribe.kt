package com.example.test_retrofit

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun main() {
    Single.just(1)
        .subscribeOn(Schedulers.io())
        .map {
            printThread("Map")
            it
        }
        .observeOn(Schedulers.newThread())
        .subscribe(object : SingleObserver<Int> {
            override fun onSuccess(t: Int?) {
               printThread("onSuccess")
            }

            override fun onSubscribe(d: Disposable?) {
                printThread("onSubscribe")

            }

            override fun onError(e: Throwable?) {
                printThread("onError")

            }

        })

    Thread.sleep(100)
}