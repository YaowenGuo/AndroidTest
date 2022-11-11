
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
#include "utils/native_debug.h"
#include "peer/my_rtc_engine.h"

#define CameraActivity(func, ...) \
  Java_tech_yaowen_rtc_1native_view_home_CameraActivity_##func (__VA_ARGS__)


#define RTCEngine(func, ...) \
  Java_tech_yaowen_rtc_1native_rtc_RTCEngine_ ## func (__VA_ARGS__)

std::string jstring2str(JNIEnv *jni, jstring jstr) {
    jboolean copy = false;
    const char *charArr = jni->GetStringUTFChars(jstr, &copy);
    std::string str(charArr);
    jni->ReleaseStringUTFChars(jstr, charArr);
    return str;
}

extern "C" JNIEXPORT void JNICALL
CameraActivity(takePhoto, JNIEnv *env, jclass type) {
    RTC_CHECK(pLiveObj) << "Live is null";
    auto camera = pLiveObj->GetCamera().lock();
    if (camera) {
        camera->TakePhoto();
    }
}
//
//extern "C" JNIEXPORT void JNICALL
//CameraActivity(OnExposureChanged, JNIEnv *env, jobject instance, jlong exposurePercent) {
//    GetAppEngine()->OnCameraParameterChanged(ACAMERA_SENSOR_EXPOSURE_TIME,
//                                             exposurePercent);
//}
//
//extern "C" JNIEXPORT void JNICALL
//CameraActivity(OnSensitivityChanged, JNIEnv *env, jobject instance, jlong sensitivity) {
//    GetAppEngine()->OnCameraParameterChanged(ACAMERA_SENSOR_SENSITIVITY,
//                                             sensitivity);
//}


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
//    THREAD_CURRENT("Answer");
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->connectToPeer(false);
}


extern "C" JNIEXPORT void JNICALL
RTCEngine(setRemoteDescription, JNIEnv *env, jclass clazz, jint type, jstring jSd) {
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->onSDPReceived((webrtc::SdpType)type, jstring2str(env, jSd));

}


extern "C" JNIEXPORT void JNICALL
RTCEngine(setIce, JNIEnv *env, jclass jclazz, jstring sdp_mid, jint sdp_mline_index, jstring sdp) {
    RTC_CHECK(pLiveObj) << "Live is null";
    pLiveObj->onIceCandidateReceived(jstring2str(env, sdp_mid), sdp_mline_index, jstring2str(env, sdp));
}