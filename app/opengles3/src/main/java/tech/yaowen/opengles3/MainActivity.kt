package tech.yaowen.opengles3

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.yaowen.opengles3.databinding.ActivityMainBinding
import tech.yaowen.opengles3.renderer.SimpleRenderer

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

        val renderer = SimpleRenderer()
        glSurfaceView.setRenderer(renderer)



//        val textureView = TextureView(this)
//        setContentView(textureView)
//
//        textureView.setSurfaceTexture(glSurfaceView)
//
//        textureView.setEGLContextClientVersion(3)
//
//        val renderer = NativeColorRenderer(Color.RED)
//        textureView.setRenderer(renderer)
    }


}
