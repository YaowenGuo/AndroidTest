package tech.yaowen.test_jni;

public class TestJni {
    static {
        System.loadLibrary("native-lib");
    }


    native String getHello();

    native String testH();
}
