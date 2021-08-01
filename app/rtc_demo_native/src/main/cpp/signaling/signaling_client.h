//
// Created by Albert on 2021/7/28.
//

#ifndef ANDROIDTEST_SIGNALING_CLIENT_H
#define ANDROIDTEST_SIGNALING_CLIENT_H

#include <string>
#include <api/jsep.h>

#include "socket_callback_interface.h"
#include "../../../../../../lib/socket.io-client-cpp/src/sio_client.h"

using std::string;
using webrtc::IceCandidateInterface;
using webrtc::SessionDescriptionInterface;
using sio::message;

namespace rtc_demo {
    class SignalingClient {
    public:
        SignalingClient(SocketCallbackInterface *);


        ~SignalingClient();


        void join(string const &name);


        void leave();


        void SendIceCandidate(string const &candidate);


        void SendSessionDescription(string const &desc);


    private:
        sio::client client_;
        SocketCallbackInterface *callback_ = nullptr;
    };


    void printMsgLog(string prefix, message::ptr const &data);
}
#endif //ANDROIDTEST_SIGNALING_CLIENT_H
