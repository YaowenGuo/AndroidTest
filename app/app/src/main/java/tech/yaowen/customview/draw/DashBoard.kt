package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import tech.yaowen.customview.dpToPx

open class DashBoard : View {

    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)

    val radius = 150.dpToPx()

    val dashArea = RectF()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path()
    val dash = RectF(0f, 0f, 2.dpToPx(), 10.dpToPx())
    val borderPath = Path()
    var centerX = 0f
    var centerY = 0f


    lateinit var pathEffect: PathEffect

    init {
        paint.strokeWidth = 10f
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        dashArea.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        path.addRect(dash, Path.Direction.CW)
        borderPath.addArc(dashArea, 135f, 270f)
        pathEffect = PathDashPathEffect(
            path,
            (PathMeasure(borderPath, false).length - 2.dpToPx()) / 20,
            0f, PathDashPathEffect.Style.ROTATE
        )
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.drawPath(borderPath, paint)
        paint.pathEffect = pathEffect
        paint.strokeWidth = 10f
        canvas?.drawPath(borderPath, paint)
        paint.pathEffect = null

        canvas?.drawLine(
            centerX, centerY,
            centerX + radius / 2 * Math.cos(Math.toRadians(150.0)).toFloat(),
            centerY + radius / 2 * Math.sin(Math.toRadians(150.0)).toFloat(),
            paint
        )

    }
}