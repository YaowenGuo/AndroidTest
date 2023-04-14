package tech.yaowen.lib_share.device

import android.app.ActivityManager
import android.content.Context


class MemoryInfo {
    companion object {
        fun memoryLimit(context: Context) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.memoryClass
        }
    }
}