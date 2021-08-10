//
// Created by Albert on 2021/7/28.
//

#ifndef ANDROIDTEST_SIGNALING_CLIENT_H
#define ANDROIDTEST_SIGNALING_CLIENT_H


#include <api/jsep.h>


#include "socket_callback_interface.h"

using std::string;

namespace rtc_demo {

    class SignalingClientWrapper {
    public:
        SignalingClientWrapper(JNIEnv *jni, jobject &instance);


        ~SignalingClientWrapper();


        void SendIceCandidate(string const &candidate);


        void SendSessionDescription(string const &desc);


    private:
        JavaVM *vm_;
        jobject j_signaling_client_;
        jmethodID j_send_session_method;
        jmethodID j_send_ice_method;
    };
}
#endif //ANDROIDTEST_SIGNALING_CLIENT_H
