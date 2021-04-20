package tech.yaowen.opengles3_native.base

enum class NativeLib {
    LIB;
    init {
        System.loadLibrary("gl_test")
    }
}