package tech.yaowen.testrxjava

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.functions.Consumer

fun main() {
    Single.just(0)
        .doOnSubscribe(Consumer {

        })
        .subscribe()
}