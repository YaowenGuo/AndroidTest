package tech.yaowen.opengles3.renderer

import android.graphics.Color
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ColorRenderer(private val color: Int = 0): GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val redF = Color.red(color) .toFloat()/ 255
        val greenF = Color.green(color).toFloat() / 255
        val blueF = Color.blue(color).toFloat() / 255
        val alphaF = Color.alpha(color).toFloat() / 255
        GLES30.glClearColor(redF, greenF, blueF, alphaF)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
    }
}