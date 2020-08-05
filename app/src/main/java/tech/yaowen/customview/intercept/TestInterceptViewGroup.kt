package tech.yaowen.customview.intercept

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout

class TestInterceptViewGroup : FrameLayout {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        Log.e("TestInterceptViewGroup", "create")
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("TestInterceptViewGroup", "event ${ev!!.actionIndex}")
        return super.dispatchTouchEvent(ev)
    }

    var moveTime = 0
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        Log.e("TestInterceptViewGroup", "event $moveTime")
        return when(event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                moveTime = 0
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                moveTime += 1
                moveTime > 5
            }
            else -> false
        }

    }
}