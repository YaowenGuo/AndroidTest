//
// Created by 2 1 on 2021/4/15.
//

#include <api/create_peerconnection_factory.h>
#define _LIBCPP_NAMESPACE _LIBCPP_CONCAT(__,_LIBCPP_ABI_VERSION)

void createEngine() {
     webrtc::CreatePeerConnectionFactory(
            nullptr /* network_thread */, nullptr /* worker_thread */,
            nullptr /* signaling_thread */, nullptr /* default_adm */,
            nullptr,
            nullptr,
            nullptr,
            nullptr,
            nullptr /* audio_mixer */,
            nullptr /* audio_processing */);
}