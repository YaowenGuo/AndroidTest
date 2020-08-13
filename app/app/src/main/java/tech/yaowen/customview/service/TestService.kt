package tech.yaowen.customview.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import tech.yaowen.customview.R
import tech.yaowen.customview.ui.service.ServiceTestActivity
import java.util.*

class TestService : Service() {
    companion object {
        val TAG = "TestService"
        val ONGOING_NOTIFICATION_ID: Int = 1
        val CHANNEL_DEFAULT_IMPORTANCE: String = "Test service"
    }


    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Log.e(TAG, "HANDEL MESSAGE------------")
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "START------------")

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

//        serviceHandler?.obtainMessage()?.also { msg ->
//            Log.e(TAG, "MESSAGE------------")
//
//            msg.arg1 = startId
//            serviceHandler?.sendMessage(msg)
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService()
        }
        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return binder
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startForegroundService() {
        val pendingIntent: PendingIntent =
            Intent(this, ServiceTestActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(manager, CHANNEL_DEFAULT_IMPORTANCE)
        val notification: Notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        manager: NotificationManager,
        channelId: String
    ) {

        val notificationChannel = NotificationChannel(
            channelId,
            "Test", NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Notification"
        manager.createNotificationChannel(notificationChannel)

    }


    // Binder given to clients
    private val binder = LocalBinder()

    // Random number generator
    private val mGenerator = Random()

    /** method for clients  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): TestService = this@TestService
    }
}