package tech.yaowen.customview.intercept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import tech.yaowen.customview.R

class InterceptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intercept)
        findViewById<TextView>(R.id.textView)
            .setOnTouchListener { v, event ->
                Log.e("TestInterceptViewGroup", "touch")
                true
            }

    }

    fun testClick(view: View) {
        Log.e("TestInterceptViewGroup", "Click")
    }

    fun testThreadRun() {
        while(true) {
            Thread.sleep(3000)
            TestThread().start()

        }
    }

    class TestThread: Thread() {
        override fun run() {
            Thread.sleep(1000)
        }
    }
}