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
#include "utils/jvm.h"

using std::string;

namespace rtc_demo {

    SignalingClientWrapper::SignalingClientWrapper(JNIEnv *jni, jobject &instance) {
        JavaVM *jvm =  jni::GetJVM();
        AttachThreadScoped ats(jvm);
        JNIEnv *env = ats.env();

        j_signaling_client_ = instance;
        jclass j_class_CoreDispatcher = env->FindClass(
                "tech/yaowen/rtc_native/signaling/SignalingClient");
        j_send_session_method = env->GetMethodID(j_class_CoreDispatcher, "sendSd",
                                                 "(Lorg/webrtc/SessionDescription;)V");
        j_send_ice_method = env->GetMethodID(j_class_CoreDispatcher, "sendIce",
                                             "(Lorg/webrtc/IceCandidate;)V");
    }


    SignalingClientWrapper::~SignalingClientWrapper() {
        AttachThreadScoped ats(jni::GetJVM());
        JNIEnv *env = ats.env();
        env->DeleteGlobalRef(j_signaling_client_);
    }


    void SignalingClientWrapper::SendSessionDescription(const SessionDescriptionInterface *desc) {
        AttachThreadScoped ats(jni::GetJVM());
        JNIEnv *env = ats.env();
        jclass sd_class = env->FindClass("org/webrtc/SessionDescription");
        ASSERT(sd_class != nullptr, "FindClass return null pointer: %s, %d", __FUNCTION__, __LINE__)
        jmethodID session_constructor = env->GetMethodID(
                sd_class, "<init>",
                "(Lorg/webrtc/SessionDescription$Type;Ljava/lang/String;)V"
        );

        jclass sd_type_class = env->FindClass("org/webrtc/SessionDescription$Type");
        jfieldID field_type_id = env->GetStaticFieldID(
                sd_type_class, "OFFER",
                "Lorg/webrtc/SessionDescription$Type;"
        );
        jobject fieldType = env->GetStaticObjectField(sd_type_class, field_type_id);

        std::string sdp;
        desc->ToString(&sdp);
        jobject sd = env->NewObject(
                sd_class, session_constructor, fieldType,
                env->NewStringUTF(sdp.c_str()));
        env->CallVoidMethod(j_signaling_client_, j_send_session_method, sd);
        env->DeleteGlobalRef(fieldType);
        env->DeleteGlobalRef(sd);
        delete desc;
    }


    void SignalingClientWrapper::SendIceCandidate(const IceCandidateInterface *candidate) {
        AttachThreadScoped ats(jni::GetJVM());
        JNIEnv *env = ats.env();
        RTC_LOG(INFO) << __FUNCTION__ << " " << candidate->sdp_mline_index();
        jclass j_ice_class = env->FindClass("org/webrtc/IceCandidate");
        jmethodID j_session_constructor = env->GetMethodID(
                j_ice_class, "<init>",
                "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Lorg/webrtc/PeerConnection$AdapterType;)V"
        );
        // sdp_mic
        jobject j_sdp_mid = env->NewStringUTF(candidate->sdp_mid().c_str());
        // sdp_mline_index
        auto j_sdp_mline_index = candidate->sdp_mline_index();
        // sdp
        string sdp;
        candidate->ToString(&sdp);
        jobject j_sdp = env->NewStringUTF(sdp.c_str());
        // server_url
        jobject j_server_url = env->NewStringUTF(candidate->server_url().c_str());
        // adapter_type
        jclass j_adapter_type_class = env->FindClass("org/webrtc/PeerConnection$AdapterType");
        jfieldID j_adapter_type_id = env->GetStaticFieldID(
                j_adapter_type_class, "UNKNOWN",
                "Lorg/webrtc/PeerConnection$AdapterType;"
        );
        jobject j_adapter_type = env->GetStaticObjectField(j_adapter_type_class, j_adapter_type_id);

        // ice
        jobject j_ice = env->NewObject(
                j_ice_class, j_session_constructor,
                j_sdp_mid, j_sdp_mline_index, j_sdp, j_server_url, j_adapter_type
        );
        env->CallVoidMethod(j_signaling_client_, j_send_ice_method, j_ice);
        delete j_sdp_mid;
        delete j_sdp;
        delete j_server_url;
        delete j_adapter_type;
        delete j_ice;
    }
}