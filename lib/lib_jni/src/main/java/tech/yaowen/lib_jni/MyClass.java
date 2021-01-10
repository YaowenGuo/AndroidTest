package tech.yaowen.lib_jni;

public class MyClass {

    public native void print();

    static {
        System.loadLibrary("hello");
    }

    public static void main(String[] argus) {
        MyClass my = new MyClass();
        my.print();
    }
}