package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import tech.yaowen.customview.R


open class XformView : View {
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
            //设置背景色
            canvas.drawARGB(255, 139, 197, 186)

            val saved = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)

            val canvasWidth = width.toFloat()
            val r = canvasWidth / 3
            //正常绘制黄色的圆形
            paint.color = 0xFFFFCC44.toInt()
            canvas.drawCircle(r, r, r, paint)
            //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.color = 0xFF66AAFF.toInt()
            canvas.drawRect(r, r, r * 2.7f, r * 2.7f, paint)
            //最后将画笔去除Xfermode
            paint.xfermode = null
            canvas.restoreToCount(saved)
        }
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }
}