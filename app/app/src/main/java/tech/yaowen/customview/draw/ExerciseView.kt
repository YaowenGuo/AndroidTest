package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import tech.yaowen.customview.dpToPx
import tech.yaowen.customview.pxToDp

class ExerciseView : View {

    companion object {
        const val TAG = "ExerciseView"
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius = 0f
    private var circleWidth = 20.dpToPx()

    private var text = "Exercise Doing"
    private var textSize = 30.dpToPx()

    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
//        paint.color = Color
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = width / 6f * 2
    }


    override fun onDraw(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = circleWidth
        canvas?.drawCircle(width / 2f, height / 2f, radius, paint)

        paint.color = Color.RED
        val centerX = width / 2f
        val centerY = height / 2f
        canvas?.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            -110f, 265f, false, paint)

        paint.measureText(text)
        paint.fontMetrics.bottom
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 2f
        val ass = paint.fontMetrics

        Log.e(TAG, "paint: -------------------")

        Log.e(TAG, "text ascent: " + paint.ascent())
        Log.e(TAG, "text descent: " + paint.descent())


        Log.e(TAG, "matrix: -------------------")
        Log.e(TAG, "textSize: $textSize")
        Log.e(TAG, "textHeight: " + (paint.fontMetrics.bottom  - paint.fontMetrics.top))
        Log.e(TAG, "text top: " + paint.fontMetrics.top)
        Log.e(TAG, "text bottom: " + paint.fontMetrics.bottom)
        Log.e(TAG, "text ascent: " + paint.fontMetrics.ascent)
        Log.e(TAG, "text descent: " + paint.fontMetrics.descent)
        Log.e(TAG, "text leading: " + paint.fontMetrics.leading)


        canvas?.drawText(text, centerX, centerY - (paint.fontMetrics.bottom  + paint.fontMetrics.top) / 2
        , paint)


    }
}