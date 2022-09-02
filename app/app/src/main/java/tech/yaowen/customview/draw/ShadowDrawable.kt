package tech.yaowen.customview.draw

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat.OPAQUE
import android.graphics.drawable.Drawable

class ShadowDrawable: Drawable() {
    private var alphaValue: Int = 0
    override fun draw(canvas: Canvas) {
    }

    override fun setAlpha(alpha: Int) {
        alphaValue = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return OPAQUE
    }
}