package tech.yaowen.customview.draw

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.util.Log
import tech.yaowen.customview.dpToPx
import tech.yaowen.customview.inchWithDp


class ClipView : View {

    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)

    private val bitmapOption = BitmapFactory.Options().also {
        it.inJustDecodeBounds = true
    }
    private var imageMap: Bitmap

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var leftMargin = 0f

    private val camera = Camera()
    private val xMatrix = Matrix()

    init {
        BitmapFactory.decodeResource(resources, tech.yaowen.customview.R.drawable.guide, bitmapOption)
        bitmapOption.inJustDecodeBounds = false
        bitmapOption.inDensity = bitmapOption.outWidth
        bitmapOption.inTargetDensity = 200.dpToPx().toInt()
        imageMap = BitmapFactory.decodeResource(resources, tech.yaowen.customview.R.drawable.guide, bitmapOption)

        camera.setLocation(0f, 0f, (-6f).inchWithDp())
        camera.rotateX(30f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        leftMargin = (w - imageMap.width) / 2f
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.apply {
            val centerX = leftMargin + imageMap.width / 2f
            val centerY = leftMargin + imageMap.height / 2f

            save()
            translate(centerX, centerY)
            rotate(-30f)
            clipRect(-imageMap.width.toFloat(), -imageMap.height.toFloat(), imageMap.width.toFloat(), 0f)
            rotate(30f)
            translate(-centerX, -centerY)
            drawBitmap(imageMap, leftMargin, leftMargin, paint)
            restore()

            save()
            translate(centerX, centerY)
            rotate(-30f)
            camera.applyToCanvas(canvas)
            clipRect(-imageMap.width.toFloat(), 0f, imageMap.width.toFloat(), imageMap.height.toFloat())
            rotate(30f)
            translate(-centerX, -centerY)
            drawBitmap(imageMap, leftMargin, leftMargin, paint)
            restore()
        }
    }
}