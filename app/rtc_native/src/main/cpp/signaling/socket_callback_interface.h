//
// Created by Albert on 2021/7/28.
//

#ifndef ANDROIDTEST_SOCKET_CALLBACK_INTERFACE_H
#define ANDROIDTEST_SOCKET_CALLBACK_INTERFACE_H

#include <string>

using std::string;
using webrtc::SdpType;

namespace rtc_demo {
    class SocketCallbackInterface {
    public:
        virtual void onSDPReceived(const SdpType type, const string &sd) = 0;


        virtual void onIceCandidateReceived(const std::string& sdp_mid, int sdp_mline_index,
                                            const std::string& sdp) = 0;
    };

}


#endif //ANDROIDTEST_SOCKET_CALLBACK_INTERFACE_H
