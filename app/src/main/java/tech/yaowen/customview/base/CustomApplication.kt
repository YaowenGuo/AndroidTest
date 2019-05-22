package tech.yaowen.customview.base

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import tech.yaowen.customview.MainActivity
import tech.yaowen.customview.R

class CustomApplication : Application() {

    companion object {
        private val channelId = "1"//消息通道的ID，以后可以通过该ID找到该消息通道
        private val channelName = "igDownload"//消息通道的名字
    }

    /**
     * Android 9.0 上 onCreate 被调用多次，不知道是系统原因还是其它引起的
     */
    override fun onCreate() {
        super.onCreate()

        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener {
            try {
                val cb = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                if (cb.hasPrimaryClip()) {
                    val cd = cb.primaryClip
                    if (cd?.getItemAt(0) != null && cd.getItemAt(0).text != null) {
                        shownDownloadCompleteNotification("HHHH", "hshhd")

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    private fun shownDownloadCompleteNotification(notification: String, contents: String) {

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // pending implicit intent to view url
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.action = Intent.ACTION_VIEW
        val addCategory = resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent.data = Uri.parse(contents)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = Notification.Builder(this)
            //.setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_keyboard_arrow_left_black_24dp)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setContentTitle(notification)
            .setAutoCancel(true)
            .setContentText(contents)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(Color.parseColor("#58c1cd"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mNotificationManager.deleteNotificationChannel("igdownload");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        } else {
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
        }
        mNotificationManager.notify(1, notificationBuilder.build())


    }




}