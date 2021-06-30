//
// Created by Albert on 2021/5/11.
//

#include "Permission.h"
#include "../base/esUtil.h"
#include "../base/env.h"
#include "../capture/Camera.h"
#include "../peer/my_rtc_engine.h"

/**
 * Initiate a Camera Run-time usage request to Java side implementation
 *  [ The request result will be passed back in function
 *    notifyCameraPermission()]
 */

void Permission::PermissionResult(JNIEnv *jni, jobject j_live, bool granted, jobject context) {
    LOGI("PermissionResult %d", granted);
    if (rtc_demo::app == nullptr) {
        LOGE("App app is not initialized");
    }

    rtc_demo::camera = new Camera(rtc_demo::app, ACAMERA_LENS_FACING_BACK);
    Live live(jni, context); // init;
    live.createEngine(); // PeerConnectionFactory + PeerConnection.
    live.AddTracks(jni); // add audio and video track.
    live.connectToPeer(nullptr); // create offer or answer.
//    live.addIce(Json::CharReaderBuilder().build()); // create or add ice.
}