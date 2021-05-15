//
// Created by Albert on 2021/5/11.
//

#include "Permission.h"
#include "../base/esUtil.h"
#include "../base/env.h"
#include "../capture/Camera.h"

/**
 * Initiate a Camera Run-time usage request to Java side implementation
 *  [ The request result will be passed back in function
 *    notifyCameraPermission()]
 */
//static void Permission::RequestCameraPermission() {
//    if (!app_) return;
//
//    JNIEnv* env;
//    ANativeActivity* activity = app_->activity;
//    activity->vm->GetEnv((void**)&env, JNI_VERSION_1_6);
//
//    activity->vm->AttachCurrentThread(&env, NULL);
//
//    jobject activityObj = env->NewGlobalRef(activity->clazz);
//    jclass clz = env->GetObjectClass(activityObj);
//    env->CallVoidMethod(activityObj,
//                        env->GetMethodID(clz, "requestCamera", "()V"));
//    env->DeleteGlobalRef(activityObj);
//
//    activity->vm->DetachCurrentThread();
//}


void Permission::PermissionResult(bool granted) {
    LOGI("PermissionResult %d", granted);
    if (rtc_demo::app == nullptr) {
        LOGE("App app is not initialized");
    } {
        rtc_demo::camera = new Camera(rtc_demo::app, ACAMERA_LENS_FACING_BACK);
    }

}