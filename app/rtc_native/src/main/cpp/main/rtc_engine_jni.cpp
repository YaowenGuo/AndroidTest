
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <modules/utility/include/helpers_android.h>
#include <api/jsep.h>
#include <sdk/android/src/jni/audio_device/audio_device_module.h>
#include <utils/jvm.h>
#include <sdk/android/native_api/jni/class_loader.h>
#include "camera/camera_engine.h"
#include "utils/native_debug.h"
#include <iostream>

#define CameraActivity(func, ...) \
  Java_tech_yaowen_rtc_1native_view_home_CameraActivity_##func (__VA_ARGS__)


#define RTCEngine(func, ...) \
  Java_tech_yaowen_rtc_1native_rtc_RTCEngine_ ## func (__VA_ARGS__)

/**
 * Retrieve current rotation from Java side
 *
 * @return current rotation angle
 */
int CameraEngine::GetDisplayRotation() {
    ASSERT(app_, "Application is not initialized");

    JNIEnv *env;
    ANativeActivity *activity = app_->activity;
    int status = activity->vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    bool isAttached = false;
    if (status < 0) {
        if (activity->vm->AttachCurrentThread(&env, NULL)){
            return 0;
        }
        isAttached = true;
    }

    jobject activityObj = env->NewGlobalRef(activity->clazz);
    jclass clz = env->GetObjectClass(activityObj);
    jint newOrientation = env->CallIntMethod(
            activityObj, env->GetMethodID(clz, "getRotationDegree", "()I"));
    env->DeleteGlobalRef(activityObj);

    if (isAttached) {
        activity->vm->DetachCurrentThread();
    }
    return newOrientation;
}


/**
 * Initializate UI on Java side. The 2 seekBars' values are passed in
 * array in the tuple of ( min, max, curVal )
 *   0: exposure min
 *   1: exposure max
 *   2: exposure val
 *   3: sensitivity min
 *   4: sensitivity max
 *   5: sensitivity val
 */
const int kInitDataLen = 6;


void CameraEngine::EnableUI(void) {
    JNIEnv *jni = env_;
    int64_t range[3];

    // Default class retrieval
    jclass clazz = jni->GetObjectClass(app_->activity->clazz);
    jmethodID methodID = jni->GetMethodID(clazz, "EnableUI", "([J)V");
    jlongArray initData = jni->NewLongArray(kInitDataLen);

    ASSERT(initData && methodID, "JavaUI interface Object failed(%p, %p)",
           methodID, initData);

    if (!camera_->GetExposureRange(&range[0], &range[1], &range[2])) {
        memset(range, 0, sizeof(int64_t) * 3);
    }

    jni->SetLongArrayRegion(initData, 0, 3, range);

    if (!camera_->GetSensitivityRange(&range[0], &range[1], &range[2])) {
        memset(range, 0, sizeof(int64_t) * 3);
    }
    jni->SetLongArrayRegion(initData, 3, 3, range);
    jni->CallVoidMethod(app_->activity->clazz, methodID, initData);
}


/**
 * Handles UI request to take a photo into
 *   /sdcard/DCIM/Camera
 */
void CameraEngine::OnTakePhoto() {
    if (camera_) {
        camera_->TakePhoto();
    }
}


void CameraEngine::OnPhotoTaken(const char *fileName) {
    JNIEnv *jni;
    app_->activity->vm->AttachCurrentThread(&jni, NULL);

    // Default class retrieval
    jclass clazz = jni->GetObjectClass(app_->activity->clazz);
    jmethodID methodID = jni->GetMethodID(clazz, "OnPhotoTaken", "(Ljava/lang/String;)V");
    jstring javaName = jni->NewStringUTF(fileName);

    jni->CallVoidMethod(app_->activity->clazz, methodID, javaName);
    app_->activity->vm->DetachCurrentThread();
}


/**
 * Process user camera and disk writing permission
 * Resume application initialization after user granted camera and disk usage
 * If user denied permission, do nothing: no camera
 *
 * @param granted user's authorization for camera and disk usage.
 * @return none
 */
void CameraEngine::OnCameraPermission(JNIEnv *env, jboolean granted, jobject context) {
//    cameraGranted_ = (granted != JNI_FALSE);
    env_ = env;
    context_ = context;

//    if (cameraGranted_) {
        OnAppInitWindow();
//    }
}


std::string jstring2str(JNIEnv *jni, jstring jstr) {
    jboolean copy = false;
    const char *charArr = jni->GetStringUTFChars(jstr, &copy);
    std::string str(charArr);
    jni->ReleaseStringUTFChars(jstr, charArr);

    return str;
}


/**
 *  A couple UI handles ( from UI )
 *      user camera and disk permission
 *      exposure and sensitivity SeekBars
 *      takePhoto button
 */
extern "C" JNIEXPORT void JNICALL
CameraActivity(notifyCameraPermission, JNIEnv *env, jclass type, jboolean permission,
               jobject context) {
    GetAppEngine()->OnCameraPermission(env, permission, context);
//  std::thread permissionHandler(&CameraEngine::OnCameraPermission,
//                                GetAppEngine(), env, permission, context);
//  permissionHandler.detach();
}

extern "C" JNIEXPORT void JNICALL
CameraActivity(TakePhoto, JNIEnv *env, jclass type) {
    std::thread takePhotoHandler(&CameraEngine::OnTakePhoto, GetAppEngine());
    takePhotoHandler.detach();
}

extern "C" JNIEXPORT void JNICALL
CameraActivity(OnExposureChanged, JNIEnv *env, jobject instance, jlong exposurePercent) {
    GetAppEngine()->OnCameraParameterChanged(ACAMERA_SENSOR_EXPOSURE_TIME,
                                             exposurePercent);
}

extern "C" JNIEXPORT void JNICALL
CameraActivity(OnSensitivityChanged, JNIEnv *env, jobject instance, jlong sensitivity) {
    GetAppEngine()->OnCameraParameterChanged(ACAMERA_SENSOR_SENSITIVITY,
                                             sensitivity);
}

extern "C" JNIEXPORT void JNICALL
CameraActivity(release, JNIEnv *env, jobject activity) {
    if (pLiveObj != nullptr) {
        delete pLiveObj;
    }
}


extern "C" JNIEXPORT void JNICALL
RTCEngine(captureAudioAndVideo, JNIEnv *env, jclass clazz, jobject application_context, jobject jSignaling) {
    RTC_CHECK(pLiveObj == nullptr) << "Live is not null";
    if (pLiveObj == nullptr) {
        auto signaling = new rtc_demo::JavaRTCEngine(env, jSignaling);
        pLiveObj = new Live(env, application_context, signaling);
        pLiveObj->createEngine(env, application_context);
    }
}


extern "C" JNIEXPORT void JNICALL
RTCEngine(call, JNIEnv *env, jclass clazz) {
    RTC_CHECK(pLiveObj) << "Live is null";
    RTC_DLOG(LS_INFO) << "Lim webrtc start Call, yaowen.";
    pLiveObj->connectToPeer(true);
}

extern "C" JNIEXPORT void JNICALL
RTCEngine(answer, JNIEnv *env, jclass clazz) {
    THREAD_CURRENT("Answer");
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->connectToPeer(false);
}


extern "C" JNIEXPORT void JNICALL
RTCEngine(setRemoteDescription, JNIEnv *env, jclass clazz, jint type, jstring jSd) {
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->onSDPReceived((webrtc::SdpType)type, jstring2str(env, jSd););

}


extern "C" JNIEXPORT void JNICALL
RTCEngine(setIce, JNIEnv *env, jclass jclazz, jstring sdp_mid, jint sdp_mline_index, jstring sdp) {
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->onIceCandidateReceived(jstring2str(env, sdp_mid), sdp_mline_index, jstring2str(env, sdp));
}