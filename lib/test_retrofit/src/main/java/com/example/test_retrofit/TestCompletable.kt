package com.example.test_retrofit

import com.example.test_retrofit.net.Client
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun main() {
/*
    Observable.create(ObservableOnSubscribe<Any> { emitter ->
        emitter?.onNext(null)
        emitter?.onComplete()
    })
        .ignoreElements()
        .subscribe(object : CompletableObserver {
            override fun onComplete() {
                println("complete")
            }

            override fun onSubscribe(d: Disposable?) {
                println("subscribe")

            }

            override fun onError(e: Throwable?) {
                println("onError")
            }

        })



    object : Observable<String>() {
        override fun subscribeActual(observer: Observer<in String>?) {
            observer?.onSubscribe(Disposable.disposed())
            observer?.onNext(null)
            observer?.onComplete()
        }
    }
        .ignoreElements()
        .subscribe(object : CompletableObserver {
            override fun onComplete() {
                println("complete")
            }

            override fun onSubscribe(d: Disposable?) {
                println("subscribe")

            }

            override fun onError(e: Throwable?) {
                println("onError")
            }

        })
*/


    Client.getServerApi()
        .testEmptyBody()
        .subscribe(object : CompletableObserver {
            override fun onComplete() {
                println("complete")
            }

            override fun onSubscribe(d: Disposable) {
                println("subscribe")

            }

            override fun onError(e: Throwable) {
                println("onError")
            }

        })

    Thread.sleep(50000)

}