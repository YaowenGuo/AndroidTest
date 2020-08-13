package tech.yaowen.testrxjava

import io.reactivex.rxjava3.annotations.Nullable
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.EmptyDisposable
import io.reactivex.rxjava3.internal.fuseable.QueueDisposable
import io.reactivex.rxjava3.internal.fuseable.QueueFuseable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit

fun main() {

/*    Observable.interval(1, TimeUnit.SECONDS)
        .subscribeBy(  // named arguments for lambda Subscribers
            onNext = { println(it) },
            onError = { it.printStackTrace() },
            onComplete = { println("Done!") }
        )

    //Some Emission
    val singleSource = Maybe.just("single item")

    singleSource.subscribeBy(
        onSuccess = { s -> println("Item received: from singleSource $s") },
        onError = { obj: Throwable -> obj.printStackTrace() },
        onComplete = { println("Done from SingleSource") }
    )


    //no emission
    val emptySource = Maybe.empty<Int>()
    emptySource.subscribeBy(
        onSuccess = { s -> println("Item received: from emptySource$s") },
        onError = { obj: Throwable -> obj.printStackTrace() },
        onComplete = { println("Done from EmptySource") }
    )

    val emptyException = Maybe.just(Exception("Ex"))
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onSuccess = { s -> println("Item received: from emptySource$s") },
            onError = { obj: Throwable -> obj.printStackTrace() },
            onComplete = { println("Done from EmptySource") }
        )

    var single: Disposable? = null
    Single.just(1)
        .map { it.toString() }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())
        .subscribe(object : SingleObserver<String> {
            override fun onSuccess(t: String?) {

            }

            override fun onSubscribe(d: Disposable?) {
                single = d
            }

            override fun onError(e: Throwable?) {

            }

        })

    single?.dispose()*/


    val list = intArrayOf(1, 2, 3, 4, 5)

    list.toObservable()
        .map { it + 2 }
        .subscribeOn(Schedulers.newThread())
        .subscribe {
            println(it)
        }




    list.toObservable()
        .flatMap {
//            IntArray(it) { index->
//                it + index;
//            }.toObservable()
            Observable.just(it)
        }
        .subscribe {
            println(it)
        }



}