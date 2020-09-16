package tech.yaowen.opengles3

import android.graphics.Color
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.yaowen.opengles3.databinding.ActivityMainBinding
import tech.yaowen.opengles3.renderer.ColorRenderer
import tech.yaowen.opengles3.renderer.NativeColorRenderer

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(3)

        val renderer = NativeColorRenderer(Color.RED)
        glSurfaceView.setRenderer(renderer)


    }


}
