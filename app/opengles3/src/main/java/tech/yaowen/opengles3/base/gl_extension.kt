package tech.yaowen.opengles3.base

import android.opengl.GLES30


fun GLES30.compileVertexShader(shaderCode: String): Int {
    return compileShader(GLES30.GL_VERTEX_SHADER, shaderCode)
}

fun GLES30.compileShader(type: Int, shaderCode: String): Int {
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