package tech.yaowen.customview.ui.workmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.*
import tech.yaowen.customview.R
import tech.yaowen.customview.ui.databinding.BindingActivity
import java.util.concurrent.TimeUnit

class JobQueueActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_queue)

        // Create a Constraints object that defines when the task should run
        /*val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(true)
            .setRequiresCharging(true)

            .build()
*/
        val workArray = ArrayList<WorkRequest>()



        for (i in 0 until 5) {
            val imageData = workDataOf("google" to "$i")
            Log.e("MyWorker", imageData.keyValueMap.toString())
            val oneOffWork = OneTimeWorkRequestBuilder<UIWorker>()

                /*.setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )*/
                .setInputData(imageData)
                .build()
            workArray.add(oneOffWork)
//            if (i == 1) {
//                WorkManager.getInstance(this)
//                    .enqueueUniqueWork("ds", ExistingWorkPolicy.REPLACE, oneOffWork)
//            }
//            WorkManager.getInstance(this)
//                .enqueueUniqueWork("ds", ExistingWorkPolicy.REPLACE, oneOffWork)
//                .enqueue(oneOffWork)
        }


        object: Thread() {
            override fun run() {
                Log.e("MyWorker", "stop")

                sleep(5000)
                if (workArray.size > 0) {
                    Log.e("MyWorker", "stop")
                    WorkManager.getInstance(this@JobQueueActivity).cancelAllWork()
                }
            }
        }.start()


       /* WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("ds")
            .observe(this, Observer { infos ->
                var i = 0
                for (info in infos) {
                    i++
                    Log.e("MyWorker", "info $i: ${info.id}: ${info.state}, ${info.tags}")
                }
            })*/


    }

    fun startSecondActivity(view: View) {

        val intent = Intent(this, BindingActivity::class.java)
        startActivity(intent)
        finish()
    }
}
