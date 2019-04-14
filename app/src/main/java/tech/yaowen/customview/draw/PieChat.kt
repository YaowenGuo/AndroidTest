package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import tech.yaowen.customview.dpToPx
import tech.yaowen.customview.pxToDp

class PieChat : View {

    var centerX = 0f
    var centerY = 0f
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
    }

    val redius = 150.dpToPx()

    val colorArr = intArrayOf(0xff8064ec.toInt(), 0xfff5ba18.toInt(), 0xff000000.toInt(), 0xff3897f0.toInt())
    val angleArr = floatArrayOf(100f, 180f, 260f, 60f)

    val rectF = RectF()

    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2f
        centerY = h / 2f
        rectF.set(centerX - redius, centerY - redius, centerX + redius, centerY + redius)
    }

    override fun onDraw(canvas: Canvas?) {
        for (i in angleArr.indices) {
            paint.color = colorArr[i]
            if (i == 1) {
                val radians = Math.toRadians((angleArr[(i + 1) % angleArr.size] + angleArr[i]) / 2.toDouble())
                canvas?.save()
                canvas?.translate(
                    Math.cos(radians).toFloat() * 50,
                    Math.sin(radians).toFloat() * 50
                )
            }
            canvas?.drawArc(rectF, angleArr[i], Math.abs(angleArr[(i + 1) % angleArr.size] - angleArr[i]), true, paint)
            if (i == 1) {
                canvas?.restore()
            }
        }
    }
}