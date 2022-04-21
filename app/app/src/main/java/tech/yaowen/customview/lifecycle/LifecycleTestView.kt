package tech.yaowen.customview.lifecycle

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View

class LifecycleTestView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(javaClass.simpleName, "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        Log.d(javaClass.simpleName, "onDetachedFromWindow")
        super.onDetachedFromWindow()
    }
}