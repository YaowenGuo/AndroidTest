package tech.yaowen.testrxjava

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun main() {
    Single.just(0)
        .doOnSubscribe(Consumer {
            println("Single: ------")

        })
        .subscribe()
    println("time: ------")

    Observable.intervalRange(0, 30, 0,1, TimeUnit.SECONDS)
        .doOnSubscribe(Consumer {
            println("Observable: ------")

        })
        .subscribeOn(Schedulers.newThread())
        .subscribe({ time ->
            println("time: $time")
        }, { exception ->

        }, {
            println("complete")
        })
    println("time: end")

    Thread.sleep(600000)
}