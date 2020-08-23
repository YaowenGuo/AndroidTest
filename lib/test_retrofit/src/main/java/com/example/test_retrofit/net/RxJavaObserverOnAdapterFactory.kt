package com.example.test_retrofit.net

import io.reactivex.rxjava3.core.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Must add before `addCallAdapterFactory(RxJava3CallAdapterFactory.createXXX(Schedulers.io()))
 */
internal class ObserveOnMainCallAdapterFactory(val scheduler: Scheduler) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        return when (getRawType(returnType)) {
            Observable::class.java, Single::class.java, Flowable::class.java, Completable::class.java,
            Maybe::class.java -> createCallAdapter(returnType, annotations, retrofit)
            else -> null
        }
    }


    fun createCallAdapter(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<Any, Any> {
        // Look up the next call adapter which would otherwise be used if this one was not present.
        val delegate: CallAdapter<Any, Any> =
            retrofit.nextCallAdapter(
                this,
                returnType,
                annotations
            ) as CallAdapter<Any, Any>

        return object : CallAdapter<Any, Any> {
            override fun adapt(call: Call<Any>): Any {
                // Delegate to get the normal Observable...
                // ...and change it to send notifications to the observer on the specified scheduler.
                return when (val o: Any = delegate.adapt(call)) {
                    is Observable<*> -> o.observeOn(scheduler)
                    is Single<*> -> o.observeOn(scheduler)
                    is Flowable<*> -> o.observeOn(scheduler)
                    is Maybe<*> -> o.observeOn(scheduler)
                    is Completable -> o.observeOn(scheduler)
                    else -> o
                }
            }

            override fun responseType(): Type {
                return delegate.responseType()
            }
        }
    }

//    fun <T> proceed(retrofit: Retrofit, call: Call<Any>): T {
//        val delegate: CallAdapter<Any, Any> =
//            retrofit.nextCallAdapter(
//                this,
//                returnType,
//                annotations
//            ) as CallAdapter<Any, Any>
//        return delegate.adapt(call)
//    }
}