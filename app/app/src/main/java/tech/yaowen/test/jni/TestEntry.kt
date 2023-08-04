package tech.yaowen.test.jni

class TestEntry {
    init {
        System.loadLibrary("test_jni")
    }

    external fun print()

    external fun printValue(value: String)

    external fun stringFromJNI(): String

}