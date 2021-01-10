package tech.yaowen.testrxjava

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import java.util.concurrent.TimeUnit

fun main(argus: Array<String>) {
    println("Time: + " + System.currentTimeMillis())
    Single.timer(5000, TimeUnit.MILLISECONDS)
        .subscribe(Consumer {
            println("Time: $it + " + System.currentTimeMillis())
        })

    Thread.sleep(6000)
    println("Time: + " + System.currentTimeMillis())
}