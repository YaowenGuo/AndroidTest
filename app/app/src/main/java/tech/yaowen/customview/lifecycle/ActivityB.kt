package tech.yaowen.customview.lifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import tech.yaowen.customview.R

class ActivityB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        Log.e("ActivityB", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.e("ActivityB", "onStart")

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("ActivityB", "onNewIntent")

    }

    override fun onRestart() {
        super.onRestart()
        Log.e("ActivityB", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.e("ActivityB", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("ActivityB", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("ActivityB", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ActivityB", "onDestroy")

    }

    fun buttonClicked(view: View) {
        startActivity(Intent(this, ActivityA::class.java))
    }
}