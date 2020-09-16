package tech.yaowen.opengles3.renderer

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NativeColorRenderer(private val color: Int = 0): GLSurfaceView.Renderer {
    companion object {
        init {
            System.loadLibrary("gl_es")
        }
    }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceCreated(color)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        onDrawFrame()
    }


    private external fun surfaceCreated(color: Int)

    private external fun surfaceChanged(width: Int, height: Int)

    private external fun onDrawFrame()
}