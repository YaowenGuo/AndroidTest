//
// Created by Albert on 2021/7/28.
//

#include <string>
#include <utils/native_debug.h>
#include <iostream>
#include <condition_variable>
#include <peer/android_video_frame_buffer.h>

#include "utils/class_loader.h"
#include "signaling_client.h"
#include "peer/my_rtc_engine.h"
#include "utils/jvm.h"
#include "utils/jni_generator_helper.h"


using std::string;

namespace rtc_demo {


    const char kClassPath_org_webrtc_SessionDescription[] = "org/webrtc/SessionDescription";

    const char kClassPath_org_webrtc_SessionDescription_00024Type[] = "org/webrtc/SessionDescription$Type";
    std::atomic<jclass> g_org_webrtc_SessionDescription_clazz(nullptr);
    inline jclass org_webrtc_SessionDescription_clazz(JNIEnv* env) {
        return LazyGetClass(env, kClassPath_org_webrtc_SessionDescription,
                            &g_org_webrtc_SessionDescription_clazz);
    }

    JavaRTCEngine::JavaRTCEngine(JNIEnv *env, jobject &instance) {
        env = jni::GetEnv();
        j_signaling_client_ = env->NewGlobalRef(instance); // Java 函数调用结束后，JNI 参数中的 jObject 会被删除，所以要新创建一个全局的。
        jclass j_class_CoreDispatcher = env->FindClass(
                "tech/yaowen/rtc_native/rtc/RTCEngine");
        j_send_session_method = env->GetMethodID(j_class_CoreDispatcher, "sendSd",
                                                 "(Lorg/webrtc/SessionDescription;)V");
        j_send_ice_method = env->GetMethodID(j_class_CoreDispatcher, "sendIce",
                                             "(Lorg/webrtc/IceCandidate;)V");

        jclass j_pc_class = env->FindClass("org/webrtc/PeerConnection");
        ASSERT(j_pc_class != nullptr, "Find PeerConnection Class return null pointer: %s, %d", __FUNCTION__, __LINE__)
        j_pc_class = jni::GetEnv()->FindClass("org/webrtc/PeerConnection");
        ASSERT(j_pc_class != nullptr, "Find PeerConnection Class return null pointer: %s, %d", __FUNCTION__, __LINE__)
    }


    JavaRTCEngine::~JavaRTCEngine() {
        jni::GetEnv()->DeleteGlobalRef(j_signaling_client_);
    }


    void JavaRTCEngine::SendSessionDescription(const SessionDescriptionInterface *desc) {
        JNIEnv* env = AttachCurrentThreadIfNeeded();

        jclass sd_class = org_webrtc_SessionDescription_clazz(env);
        ASSERT(sd_class != nullptr, "FindClass return null pointer: %s, %d", __FUNCTION__, __LINE__);
        jmethodID session_constructor = env->GetMethodID(
                sd_class, "<init>",
                "(Lorg/webrtc/SessionDescription$Type;Ljava/lang/String;)V"
        );

        auto sd_type_class = GetClass(env, "org/webrtc/SessionDescription$Type");
        jfieldID field_type_id = env->GetStaticFieldID(
                sd_type_class.obj(), "OFFER",
                "Lorg/webrtc/SessionDescription$Type;"
        );

        jobject fieldType = env->GetStaticObjectField(sd_type_class.obj(), field_type_id);
        std::string sdp;
        desc->ToString(&sdp);
        jobject sd = env->NewObject(
                sd_class, session_constructor, fieldType,
                env->NewStringUTF(sdp.c_str()));
        ASSERT(sd != nullptr, "Create SessionDescription failed: %s, %d", __FUNCTION__, __LINE__);
        THREAD_CURRENT("SendSessionDescription");
        env->CallVoidMethod(j_signaling_client_, j_send_session_method, sd);
        env->DeleteLocalRef(fieldType);
        env->DeleteLocalRef(sd);
    }


    void JavaRTCEngine::SendIceCandidate(const IceCandidateInterface *candidate) {
        JNIEnv* env = AttachCurrentThreadIfNeeded();
        RTC_LOG(LS_INFO) << __FUNCTION__ << " " << candidate->sdp_mline_index();
        auto j_ice_class = GetClass(env, "org/webrtc/IceCandidate");
        jmethodID j_session_constructor = env->GetMethodID(
                j_ice_class.obj(), "<init>",
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
        auto j_adapter_type_class = GetClass(env, "org/webrtc/PeerConnection$AdapterType");
        jfieldID j_adapter_type_id = env->GetStaticFieldID(
                j_adapter_type_class.obj(), "UNKNOWN",
                "Lorg/webrtc/PeerConnection$AdapterType;"
        );
        jobject j_adapter_type = env->GetStaticObjectField(j_adapter_type_class.obj(), j_adapter_type_id);

        // ice
        jobject j_ice = env->NewObject(
                j_ice_class.obj(), j_session_constructor,
                j_sdp_mid, j_sdp_mline_index, j_sdp, j_server_url, j_adapter_type
        );
        env->CallVoidMethod(j_signaling_client_, j_send_ice_method, j_ice);
        env->DeleteLocalRef(j_sdp_mid);
        env->DeleteLocalRef(j_sdp);
        env->DeleteLocalRef(j_server_url);
        env->DeleteLocalRef(j_adapter_type);
        env->DeleteLocalRef(j_ice);
        // TODO 为什么不能删除？
        // delete candidate;
    }
}