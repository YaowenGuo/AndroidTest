//
// Created by Albert on 2021/5/12.
//

#include <jni.h>
#include "../permissition/Permission.h"
#include "../base/esUtil.h"

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

#define JNI_VERSION JNI_VERSION_1_6

static JNINativeMethod mapMethods[] = {
        {"permissionResult", "(Z)V", reinterpret_cast<void *>(Permission::PermissionResult)}
};

int jniRegisterNativeMethods(JNIEnv *env, const char *className,
                                        const JNINativeMethod *gMethods, jint numMethods) {
    jclass c = env->FindClass(className);
    LOGE("LOGE: env->FindClass");
    if (c == nullptr) {
        LOGE("LOGE: c == nullptr");
        const char msg[] = "Native registration unable to find class '%s'; aborting...";
        env->FatalError(msg);
    }

    if (env->RegisterNatives(c, gMethods, numMethods) < 0) {
        LOGE("LOGE: RegisterNatives");
        char *msg;
//        asprintf(&msg, "RegisterNatives failed for '%s'; aborting...", className);
        env->FatalError(msg);
    }

    return 0;
}

jint JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    jvm->GetEnv((void **)(&env), JNI_VERSION);
    const char *liveClassName = "tech/yaowen/rtc_demo_native/view/home/HomeActivity";
    jclass clazz = env->FindClass(liveClassName);
//    s_jfield_nativeLive = env->GetFieldID(clazz, "permissionResult", "J");

    jniRegisterNativeMethods(env, liveClassName, mapMethods, NELEM(mapMethods));
    return JNI_VERSION;
}

