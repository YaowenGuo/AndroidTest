package tech.yaowen.opengles3.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import tech.yaowen.opengles3.base.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
class SimpleRendererCopy : GLSurfaceView.Renderer {
    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        fun compileVertexShader(shaderCode: String): Int {
            return compileShader(GLES30.GL_VERTEX_SHADER, shaderCode)
        }

        private fun compileShader(type: Int, shaderCode: String): Int {
            //创建一个着色器
            val shaderId = GLES30.glCreateShader(type)
            return if (shaderId != 0) {
                GLES30.glShaderSource(shaderId, shaderCode)
                GLES30.glCompileShader(shaderId)
                //检测状态
                val compileStatus = IntArray(1)
                GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
                if (compileStatus[0] == 0) {
                    val logInfo = GLES30.glGetShaderInfoLog(shaderId)
                    System.err.println(logInfo)
                    //创建失败
                    GLES30.glDeleteShader(shaderId)
                    return 0
                }
                shaderId
            } else {
                //创建失败
                0
            }
        }
    }

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private var mProgram = 0

    /**
     * 点的坐标
     */
    private val vertexPoints = floatArrayOf(
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    /**
     * 顶点着色器
     */
    private val vertextShader = """
#version 300 es 
uniform mat4 u_mvpMatrix

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
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints)
        vertexBuffer.position(0)
        colorBuffer = ByteBuffer.allocateDirect(color.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        //传入指定的数据
        colorBuffer.put(color)
        colorBuffer.position(0)
    }


    override fun onSurfaceCreated(
        gl: GL10,
        config: EGLConfig
    ) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)

        //编译
        val vertexShaderId = ShaderUtils.compileVertexShader(vertextShader)
        val fragmentShaderId = ShaderUtils.compileFragmentShader(fragmentShader)
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        //在OpenGLES环境中使用程序片段
        GLES30.glUseProgram(mProgram)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        //准备坐标数据
        GLES30.glVertexAttribPointer(
            0,
            POSITION_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0)

        //绘制三角形颜色
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }


}