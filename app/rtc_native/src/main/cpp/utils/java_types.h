//
// Created by Albert on 2021/12/10.
//

#ifndef ANDROIDTEST_JAVA_TYPES_H
#define ANDROIDTEST_JAVA_TYPES_H

#include <jni.h>
#include "scoped_java_ref.h"
#include "jni_generator_helper.h"

namespace rtc_demo {
    ScopedJavaLocalRef<jstring> NativeToJavaString(JNIEnv *env, const char *str);


    ScopedJavaLocalRef<jstring> NativeToJavaString(JNIEnv *jni, const std::string &str);
}

#endif //ANDROIDTEST_JAVA_TYPES_H