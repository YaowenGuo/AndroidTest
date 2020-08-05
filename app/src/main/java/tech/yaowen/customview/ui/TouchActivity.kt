package tech.yaowen.customview.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.ActivityMainBinding
import tech.yaowen.customview.databinding.ActivityTouchBinding

class TouchActivity : AppCompatActivity() {
    lateinit var binding: ActivityTouchBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityTouchBinding>(this, R.layout.activity_touch)

//        binding.view.translationX = 100f

        var handler = Handler()

        val queue = Looper.getMainLooper().getQueue()

        val translateAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_PARENT,
            0.5f
        )
        translateAnimation.duration = 1;
        translateAnimation.fillAfter = true;
        binding.view.startAnimation(translateAnimation);


        queue.addIdleHandler {
            Log.e("TouchActivity", "x: " + binding.view.x)
            Log.e("TouchActivity", "transX: " + binding.view.translationX)
            Log.e("TouchActivity", "left: " + binding.view.left)
            false
        }


    }
}