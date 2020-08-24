package tech.yaowen.testrxjava

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

fun main() {
    Single.just(0)
        .subscribe()
}