package com.example.test_retrofit

import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class SelfObserver<T>: SingleObserver<T> {
    override fun onSubscribe(d: Disposable?) {

    }

    override fun onSuccess(t: T) {
        TODO("Not yet implemented")
    }

    override fun onError(e: Throwable?) {
        TODO("Not yet implemented")
    }

}