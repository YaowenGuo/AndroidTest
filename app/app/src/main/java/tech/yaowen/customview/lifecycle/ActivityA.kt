package tech.yaowen.customview.lifecycle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import tech.yaowen.customview.R

class ActivityA : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val intent = Intent(this@ActivityA, ActivityB::class.java);
            startActivity(intent)
        }

        Log.e("ActivityA", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.e("ActivityA", "onStart")

    }

    override fun onRestart() {
        super.onRestart()
        Log.e("ActivityA", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.e("ActivityA", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("ActivityA", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("ActivityA", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ActivityA", "onDestroy")

    }

}