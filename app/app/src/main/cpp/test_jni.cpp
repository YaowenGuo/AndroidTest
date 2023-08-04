#include <jni.h>
#include <cstdio>
#include <string>
#include "util/debug_util.h"

#define TestEntry(func, ...) \
  Java_tech_yaowen_test_jni_TestEntry_##func (__VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
TestEntry(print, JNIEnv *env, jobject jTestEntry) {
//    printf("Hello world");

//    env->CallVoidMethod(jTestEntry, nullptr);
    auto strCar = env->GetStringUTFChars(nullptr, nullptr);
    std::string str("");

    env->ReleaseStringUTFChars(nullptr, strCar);

    if (strCar == nullptr) {
        LOGD("strChar is null");
    }

}


extern "C"
JNIEXPORT void JNICALL
TestEntry(printValue, JNIEnv *env, jobject jTarget, jstring value) {
    auto strCar = env->GetStringUTFChars(value, nullptr);
    if (strCar == nullptr) {
        LOGD("strChar is null");
    }
    std::string str(strCar);
    env->ReleaseStringUTFChars(value, strCar);
    LOGD("strChar value after releae %s", str.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
TestEntry(stringFromJNI, JNIEnv* env, jobject /* this */) {
    // Use-after-free error, caught by asan and hwasan.
//    int* foo = new int;
//    *foo = 3;
//    delete foo;
//    *foo = 4;

    // Signed integer overflow. Undefined behavior caught by ubsan.
    int k = 0x7fffffff;
    k += 1;
//    auto array = new int[2];
//    array[0] = array[1] + array[2];
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}