package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import tech.yaowen.customview.R


open class CustomDrawView : View {
    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val imageMap = BitmapFactory.decodeResource(resources, R.drawable.guide)
    private val imageMap2 = BitmapFactory.decodeResource(resources, R.drawable.rectangle2)
    var centerX = 0f
    var centerY = 0f

    private val bitmapShader = BitmapShader(imageMap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val shader = LinearGradient(
        100f, 100f, 500f, 500f, Color.parseColor("#E91E63"),
        Color.parseColor("#2196F3"), Shader.TileMode.MIRROR
    )
    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    init {
//        paint.style = Paint.Style.FILL_AND_STROKE
//        paint.strokeWidth = 50f
//        paint.shader = bitmapShader

        blackPaint.color = Color.RED
        blackPaint.style = Paint.Style.FILL_AND_STROKE
        blackPaint.strokeWidth = 50f

//        blackPaint.style = Paint.Style.STROKE


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
//            it.drawCircle( 500f, 500f, 400f, blackPaint)
//            paint.style = Paint.Style.STROKE
//            it.drawCircle( 700f, 500f, 400f, paint)
//            it.drawCircle( 700f, 500f, 400f, blackPaint)


//            it.drawBitmap(imageMap, 0f, 0f, paint)
            val iWight = width.toFloat()
            val iHeight = height.toFloat()
//            it.drawCircle( 500f, 500f, 400f, paint)

            val saved = canvas.saveLayer(0f, 0f, iWight, iHeight, paint)
//            canvas.drawBitmap(imageMap, 0f, 0f, paint) // 画方

            it.drawCircle( centerX, centerY, 200f, paint)
            val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            paint.xfermode = xfermode // 设置 Xfermode

            val srcWidth = imageMap2.width
            val srcHeight = imageMap2.height
            canvas.drawBitmap(imageMap, 100f, 150f, paint) // 画圆
            paint.xfermode = null // 用完及时清除 Xfermode


            canvas.restoreToCount(saved)
        }
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }
}