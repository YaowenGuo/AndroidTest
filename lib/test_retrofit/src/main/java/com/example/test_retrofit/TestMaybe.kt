package com.example.test_retrofit

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.disposables.Disposable

fun main() {
    Maybe.empty<Int>()
        .subscribe(object: MaybeObserver<Int> {
            override fun onSuccess(t: Int?) {
                println("onSuccess $t")
            }

            override fun onComplete() {
                println("onComplete")
            }

            override fun onSubscribe(d: Disposable?) {
                println("onSubscribe")
            }

            override fun onError(e: Throwable?) {
                println("onSubscribe")
            }

        })
}