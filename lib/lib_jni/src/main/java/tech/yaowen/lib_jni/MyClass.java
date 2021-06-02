package tech.yaowen.lib_jni;

public class MyClass {
    static {
        System.loadLibrary("cpp");
    }

    public native void print();
}