package tech.yaowen.lib_share.concurrence

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import java.lang.reflect.InvocationTargetException


object Process {

    @JvmStatic
    val name = fun(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        // Using reflection since ActivityThread is an internal API
        return try {
            @SuppressLint("PrivateApi") val activityThread =
                Class.forName("android.app.ActivityThread")

            // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
            // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
            val methodName =
                if (Build.VERSION.SDK_INT >= 18) "currentProcessName" else "currentPackageName"
            val getProcessName = activityThread.getDeclaredMethod(methodName)
            getProcessName.invoke(null) as String
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            ""
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            ""
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            ""
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            ""
        }
    }

}