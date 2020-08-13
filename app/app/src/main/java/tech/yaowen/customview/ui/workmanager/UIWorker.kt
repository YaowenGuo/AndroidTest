package tech.yaowen.customview.ui.workmanager

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.workDataOf

class UIWorker(ctx: Context, params: WorkerParameters): ListenableWorker(ctx, params)  {
    var resultCompleter: CallbackToFutureAdapter.Completer<Result>? = null
    // Package-private to avoid synthetic accessor.
    var imageUriInput: String? = null
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            resultCompleter = completer
            null
        }
    }

    public fun finish() {
        Log.e(this.javaClass.name, "work $imageUriInput finished")
        resultCompleter?.set(Result.success())
    }

}