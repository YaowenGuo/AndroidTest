package tech.yaowen.jni

class MyClass {
    init {
        System.loadLibrary("jni_native");
    }


    external fun print()


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MyClass().print()
        }
    }

}


//fun main() {
//    MyClass().print()
//}