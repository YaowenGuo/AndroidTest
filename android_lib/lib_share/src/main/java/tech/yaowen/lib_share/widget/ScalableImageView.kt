package tech.yaowen.lib_share.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ScaleGestureDetectorCompat
import kotlin.math.min


class ScalableImageView : AppCompatImageView, Runnable {
    companion object {
        @JvmStatic
        val OVER_SCALE_FACTOR = 1.5F
    }

    protected val gestureDetector: GestureDetectorCompat
    protected val scaleDetector: ScaleGestureDetector
    val scoller: OverScroller
    protected var isBigScale = false
    var originOffsetX = 0
    var originOffsetY = 0
    var offsetX = 0f
    var offsetY = 0f
    var bigScale = 1f
    var smallScale = 1f
    var scale = 1f
    var scaleFraction = 1f
        set(value) {
            field = value
            scale = smallScale + (bigScale - smallScale) * value
        }
    val scaleAnimator: ObjectAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        scoller = OverScroller(context)
        gestureDetector =
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (isBigScale) {
                        offsetX += distanceX
                        offsetY += distanceY
                        offsetX = min(offsetX, (drawable.intrinsicWidth * bigScale - width) / 2)
                        offsetY = min(offsetY, (drawable.intrinsicHeight * bigScale - width) / 2)
                        invalidate()
                    }
                    return true
                }

                override fun onFling(
                    down: MotionEvent?,
                    current: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    return if (isBigScale) {
                        scoller.fling(offsetX.toInt(), offsetY.toInt(), velocityX.toInt(), velocityY.toInt(),
                            - (drawable.intrinsicWidth * bigScale - width).toInt() shr 1,
                             (drawable.intrinsicWidth * bigScale - width).toInt() shr 1,
                            - (drawable.intrinsicWidth * bigScale - width).toInt() shr 1,
                            - (drawable.intrinsicWidth * bigScale - width).toInt() shr 1)

                        postOnAnimation(this@ScalableImageView)
//                        post()
                        for (i in 10 until 100 step 10) {
                            postDelayed({ refreshFling() }, i.toLong())
                        }
                        true
                    } else {
                        false
                    }
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    isBigScale = !isBigScale
                    if (isBigScale) {
                        scaleAnimator.start()
                    } else {
                        scaleAnimator.reverse()
                    }
                    return true
                }

                // 第二次点击按下的时候触发
                override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                    return true
                }

                // 第二次点击按下后的后继移动会触发
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    return performClick()
                }


            })
        gestureDetector.setIsLongpressEnabled(false)
        scaleDetector = ScaleGestureDetector(context, object:
            ScaleGestureDetector.OnScaleGestureListener {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                TODO("Not yet implemented")
            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                TODO("Not yet implemented")
            }

        })
        imageMatrix = Matrix()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val imageW = drawable.intrinsicWidth
        val imageH = drawable.intrinsicHeight
        if (imageH == 0 || imageW == 0) return

        originOffsetX = (w - imageW) shr 1
        originOffsetY = (h - imageH) shr 1

        if (w.toFloat() / h > imageW.toFloat() / imageH) {
            bigScale = w.toFloat() / imageW * OVER_SCALE_FACTOR
            smallScale = h.toFloat() / imageH
        } else {
            bigScale = h.toFloat() / imageH * OVER_SCALE_FACTOR
            smallScale = w.toFloat() / imageW
        }
        scale = if (isBigScale) bigScale else smallScale
        imageMatrix.preTranslate(offsetX * scaleFraction, offsetY * scaleFraction)
        imageMatrix.preScale(scale , scale)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }


//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//    }

    fun refreshFling() {

    }

    override fun run() {
        if (scoller.computeScrollOffset()) {
            offsetX = scoller.currX.toFloat()
            offsetY = scoller.currY.toFloat()
            invalidate()
        }
    }



    override fun computeScroll() {
        super.computeScroll()
    }

}