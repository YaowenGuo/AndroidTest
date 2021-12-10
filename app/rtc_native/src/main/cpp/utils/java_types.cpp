//
// Created by Albert on 2021/12/10.
//
#include "java_types.h"

namespace rtc_demo {
    ScopedJavaLocalRef<jstring> NativeToJavaString(JNIEnv* env, const char* str) {
        jstring j_str = env->NewStringUTF(str);
        CHECK_EXCEPTION(env) << "error during NewStringUTF";
        return ScopedJavaLocalRef<jstring>(env, j_str);
    }

    ScopedJavaLocalRef<jstring> NativeToJavaString(JNIEnv* jni,
                                                   const std::string& str) {
        return NativeToJavaString(jni, str.c_str());
    }
}