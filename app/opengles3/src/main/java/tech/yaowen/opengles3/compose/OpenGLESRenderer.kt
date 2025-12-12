package tech.yaowen.opengles3.compose

import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLUtils
import tech.yaowen.opengles3.base.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

/**
 * OpenGL ES 3.0 渲染器，用于在 Compose 中渲染
 */
class OpenGLESRenderer {
    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }

    private var egl: EGL10? = null
    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglConfig: EGLConfig? = null
    private var eglSurface: EGLSurface? = null

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private var mProgram = 0
    private var isInitialized = false

    private val vertexPoints = floatArrayOf(
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    private val vertextShader = """
#version 300 es 
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() { 
    gl_Position  = vPosition;
    gl_PointSize = 10.0;
    vColor = aColor;
}""".trimIndent()

    private val fragmentShader = """
#version 300 es 
precision mediump float;
in vec4 vColor;
out vec4 fragColor;
void main() { 
    fragColor = vColor; 
}""".trimIndent()

    private val color = floatArrayOf(
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexPoints)
        vertexBuffer.position(0)
        colorBuffer = ByteBuffer.allocateDirect(color.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        colorBuffer.put(color)
        colorBuffer.position(0)
    }

    fun initialize(width: Int, height: Int): Boolean {
        if (isInitialized) {
            return true
        }

        egl = (EGLContext.getEGL() as? EGL10) ?: return false
        eglDisplay = egl?.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

        val version = IntArray(2)
        if (egl?.eglInitialize(eglDisplay, version) == false) {
            return false
        }

        val configs = arrayOfNulls<EGLConfig>(1)
        val configAttribs = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, 0x00000040, // EGL_OPENGL_ES3_BIT
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 0,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_NONE
        )

        val numConfigs = IntArray(1)
        if (egl?.eglChooseConfig(eglDisplay, configAttribs, configs, 1, numConfigs) == false || numConfigs[0] == 0) {
            return false
        }

        eglConfig = configs[0]
        val contextAttribs = intArrayOf(
            0x3098, 3, // EGL_CONTEXT_CLIENT_VERSION
            EGL10.EGL_NONE
        )

        eglContext = egl?.eglCreateContext(
            eglDisplay,
            eglConfig,
            EGL10.EGL_NO_CONTEXT,
            contextAttribs
        )

        if (eglContext == null || eglContext == EGL10.EGL_NO_CONTEXT) {
            return false
        }

        // 创建离屏渲染表面
        val surfaceAttribs = intArrayOf(
            EGL10.EGL_WIDTH, width,
            EGL10.EGL_HEIGHT, height,
            EGL10.EGL_NONE
        )

        eglSurface = egl?.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs)
        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
            return false
        }

        if (egl?.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == false) {
            return false
        }

        // 初始化 OpenGL ES
        GLES30.glClearColor(2f, 2f, 2f, 1f)

        val vertexShaderId = ShaderUtils.compileVertexShader(vertextShader)
        val fragmentShaderId = ShaderUtils.compileFragmentShader(fragmentShader)
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        GLES30.glUseProgram(mProgram)

        GLES30.glViewport(0, 0, width, height)

        isInitialized = true
        return true
    }

    fun render(width: Int, height: Int): Bitmap? {
        if (!isInitialized) {
            if (!initialize(width, height)) {
                return null
            }
        }

        // 如果尺寸改变，需要重新创建表面
        val currentWidth = getSurfaceWidth()
        val currentHeight = getSurfaceHeight()
        if (currentWidth != width || currentHeight != height) {
            release()
            if (!initialize(width, height)) {
                return null
            }
        }

        if (egl?.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == false) {
            return null
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glVertexAttribPointer(
            0,
            POSITION_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)

        // 对于离屏渲染，不需要 swapBuffers，直接读取像素
        // 读取像素到 Bitmap
        val pixelBuffer = IntBuffer.allocate(width * height)
        GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer)
        val pixels = IntArray(width * height)
        pixelBuffer.get(pixels)

        // OpenGL 的坐标系是底部为原点，需要翻转
        val flippedPixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                flippedPixels[(height - 1 - y) * width + x] = pixels[y * width + x]
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(flippedPixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun getSurfaceWidth(): Int {
        val value = IntArray(1)
        egl?.eglQuerySurface(eglDisplay, eglSurface, EGL10.EGL_WIDTH, value)
        return value[0]
    }

    private fun getSurfaceHeight(): Int {
        val value = IntArray(1)
        egl?.eglQuerySurface(eglDisplay, eglSurface, EGL10.EGL_HEIGHT, value)
        return value[0]
    }

    fun release() {
        if (!isInitialized) {
            return
        }

        egl?.eglMakeCurrent(
            eglDisplay,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )

        egl?.eglDestroySurface(eglDisplay, eglSurface)
        egl?.eglDestroyContext(eglDisplay, eglContext)
        egl?.eglTerminate(eglDisplay)

        eglSurface = null
        eglContext = null
        eglConfig = null
        eglDisplay = null
        egl = null
        isInitialized = false
    }
}
