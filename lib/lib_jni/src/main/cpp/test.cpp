//
// Created by Albert on 2020/10/8.
//

#include "test.h"

#include <jni.h>
#include <stdio.h>
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_tech_yaowen_lib_1jni_MyClass_print(JNIEnv *env, jobject obj) {
    printf("Hello World!\n");
    return;
}

