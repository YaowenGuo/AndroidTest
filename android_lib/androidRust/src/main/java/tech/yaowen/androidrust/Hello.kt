package tech.yaowen.androidrust

class Hello {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("test_rust")
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    @Suppress("KotlinJniMissingFunction")
    external fun stringFromJNI(str: String): String


    external fun callNativeFun(): String
}