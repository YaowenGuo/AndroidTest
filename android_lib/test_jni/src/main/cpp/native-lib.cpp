#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_tech_yaowen_test_1jni_TestJni_getHello(JNIEnv *env, jobject jObj) {
    std::string hello = "Hello from Native test jni";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_tech_yaowen_test_1jni_TestJni_testH(JNIEnv *env, jobject thiz) {
    // TODO: implement testH()
}
