package tech.yaowen.opengles3

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import tech.yaowen.opengles3.compose.OpenGLES3Composable
import tech.yaowen.opengles3.renderer.SimpleRenderer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
//        setContent {
//            OpenGLES3Screen()
//        }
    }

    @Composable
    fun OpenGLES3Screen() {
        OpenGLES3Composable()
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
