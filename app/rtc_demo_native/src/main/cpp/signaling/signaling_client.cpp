//
// Created by Albert on 2021/7/28.
//

#include <string>
#include <utils/native_debug.h>
#include <iostream>
#include <condition_variable>
#include <utils/attach_thread_scoped.h>

#include "signaling_client.h"
#include "peer/my_rtc_engine.h"

using std::string;


namespace rtc_demo {

    SignalingClientWrapper::SignalingClientWrapper(JNIEnv *jni, jobject &instance) {
        JavaVM *jvm = nullptr;
        jni->GetJavaVM(&jvm);
        vm_ = jvm;

        AttachThreadScoped ats(vm_);
        JNIEnv *env = ats.env();

        j_signaling_client_ = env->NewGlobalRef(instance);
        jclass j_class_CoreDispatcher = env->FindClass(
                "tech/yaowen/rtc_demo_native/signaling/SignalingClient");
        j_send_session_method = env->GetMethodID(j_class_CoreDispatcher, "sendSessionDescription",
                                                 "(Ljava/lang/String;)V");
        j_send_ice_method = env->GetMethodID(j_class_CoreDispatcher, "sendIceCandidate",
                                             "(Ljava/lang/String;)V");

    }


    SignalingClientWrapper::~SignalingClientWrapper() {
        AttachThreadScoped ats(vm_);
        JNIEnv *env = ats.env();
        env->DeleteGlobalRef(j_signaling_client_);
    }


    void SignalingClientWrapper::SendSessionDescription(string const &sdp) {
        AttachThreadScoped ats(vm_);
        JNIEnv *env = ats.env();
        env->CallVoidMethod(j_signaling_client_, j_send_session_method, env->NewStringUTF(""));

    }


    void SignalingClientWrapper::SendIceCandidate(string const &candidate) {
        AttachThreadScoped ats(vm_);
        JNIEnv *env = ats.env();
        env->CallVoidMethod(j_signaling_client_, j_send_ice_method,
                            env->NewStringUTF(candidate.c_str()));
    }
}