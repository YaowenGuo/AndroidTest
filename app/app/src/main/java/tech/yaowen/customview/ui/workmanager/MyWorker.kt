package tech.yaowen.customview.ui.workmanager

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): ListenableWorker.Result {
        val appContext = applicationContext
        val imageUriInput = inputData.getString("google")

        for (i in 0 until  10) {
            if (isStopped) {
                Log.e(this.javaClass.name, "work $imageUriInput $i doing...")
                break
            }
            Log.e(this.javaClass.name, "work $imageUriInput $i doing...")
            Thread.sleep(1000)

        }
        val outputData = workDataOf("url" to imageUriInput)
        return Result.success(outputData)
    }


}
