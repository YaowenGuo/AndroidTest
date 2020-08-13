package tech.yaowen.customview.draw

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.view.FocusFinder
import tech.yaowen.customview.R
import tech.yaowen.customview.px2Inch


open class CameraView : View {

    fun inch2Px(inch: Float): Float {
        return inch * Resources.getSystem().displayMetrics.density
    }

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

    val camera = Camera()

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
            val canvasWidth = width.toFloat()
            val r = canvasWidth / 4

            canvas.save()
            val z = inch2Px(-8f)
            Log.e("CameraView", "z " + z)
            camera.save()
            camera.setLocation(0f, 0f, -8f)

            // 绕y轴旋转
            camera.rotateY(30f)

            val newMatrix = Matrix()
            camera.getMatrix(newMatrix)
            camera.restore()

            // 调节中心点
            newMatrix.preTranslate(- 2 * r,  - 2 * r)
            newMatrix.postTranslate( 2 * r,  2 * r)

//            camera.applyToCanvas(canvas)
            canvas.concat(newMatrix)
            paint.color = 0xFF66AAFF.toInt()
            canvas.drawRect(r, r, 3 * r, 3 * r, paint)

            canvas.restore()
        }
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }


}
