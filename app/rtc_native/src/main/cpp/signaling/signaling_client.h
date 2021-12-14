//
// Created by Albert on 2021/7/28.
//

#ifndef ANDROIDTEST_SIGNALING_CLIENT_H
#define ANDROIDTEST_SIGNALING_CLIENT_H


#include <api/jsep.h>
#include <jni.h>

#include "socket_callback_interface.h"

using std::string;
using webrtc::IceCandidateInterface;
using webrtc::SessionDescriptionInterface;

namespace rtc_demo {

    class JavaRTCEngine {
    public:
        JavaRTCEngine(JNIEnv *jni, jobject &instance);


        ~JavaRTCEngine();


        void SendIceCandidate(const IceCandidateInterface *candidate);


        void SendSessionDescription(const SessionDescriptionInterface *desc);


    protected:
        jobject j_signaling_client_;
        jmethodID j_send_session_method;
        jmethodID j_send_ice_method;
    };
}
#endif //ANDROIDTEST_SIGNALING_CLIENT_H
