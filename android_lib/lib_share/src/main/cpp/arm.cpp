#include <jni.h>
#include <jni.h>

#define Arm(fun, ...) \
    Java_tech_yaowen_lib_1share_concurrence_Arm_##fun(__VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Arm(testCAS, JNIEnv *env, jobject jActivity) {
    int lock = 1; // lock 假如不确定，是上一次的值。
    int old = 0;
    // lock 和 old 比较，相等，则写入 1，否则将 lock 值写入 old.
    // lock 写入新值（即等于 old）返回 true，否则返回 false.
    int result =  __atomic_compare_exchange_n(&lock, &old, 3 /* new value */, 0, __ATOMIC_ACQUIRE, __ATOMIC_RELAXED);
}

extern "C" JNIEXPORT void JNICALL
Arm(testHWASan, JNIEnv *env, jobject jActivity) {

}