//
// Created by 2 1 on 2021/4/15.
//

#include <api/create_peerconnection_factory.h>
#include <modules/utility/include/jvm_android.h>
#include <rtc_base/ssl_adapter.h>
#include <system_wrappers/include/field_trial.h>
#include <absl/flags/flag.h>
#include <rtc_base/physical_socket_server.h>
#include <api/audio_codecs/builtin_audio_encoder_factory.h>
#include <api/audio_codecs/builtin_audio_decoder_factory.h>
#include <api/video_codecs/builtin_video_encoder_factory.h>
#include <api/video_codecs/builtin_video_decoder_factory.h>

#define _LIBCPP_NAMESPACE _LIBCPP_CONCAT(__,_LIBCPP_ABI_VERSION)

ABSL_FLAG(
        std::string,
        force_fieldtrials,
        "",
        "Field trials control experimental features. This flag specifies the field "
        "trials in effect. E.g. running with "
        "--force_fieldtrials=WebRTC-FooFeature/Enabled/ "
        "will assign the group Enabled to field trial WebRTC-FooFeature. Multiple "
        "trials are separated by \"/\"");

void init() {
    /**
     * 1. 初始化
     */
    // InitFieldTrialsFromString stores the char*, so the char array must
    // outlive the application.
    const std::string field_trials = absl::GetFlag(FLAGS_force_fieldtrials);
    webrtc::field_trial::InitFieldTrialsFromString(field_trials.c_str());
    rtc::PhysicalSocketServer socket = rtc::PhysicalSocketServer();
    rtc::AutoSocketServerThread thread(&socket);
    rtc::InitializeSSL();

}

void createEngine(JavaVM *jvm) {
     webrtc::JVM::Initialize(jvm, nullptr);
    /**
     * 2. 创建 PeerConnectionFactory, 因为 Webrtc 可以同时进行多个连接，以创建多个 PeerConnection (PC).
     */
    rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> peer_connection_factory_ = webrtc::CreatePeerConnectionFactory(
            nullptr /* network_thread */, nullptr /* worker_thread */,
            nullptr /* signaling_thread */, nullptr /* default_adm */,
            webrtc::CreateBuiltinAudioEncoderFactory(),
            webrtc::CreateBuiltinAudioDecoderFactory(),
            webrtc::CreateBuiltinVideoEncoderFactory(),
            webrtc::CreateBuiltinVideoDecoderFactory(),
            nullptr /* audio_mixer */,
            nullptr /* audio_processing */
    );

    /**
     * 3. 创建 PC
     */
    webrtc::PeerConnectionInterface::RTCConfiguration config;
    config.sdp_semantics = webrtc::SdpSemantics::kUnifiedPlan;
    config.enable_dtls_srtp = true;
    webrtc::PeerConnectionInterface::IceServer server;
    server.uri = "stun:stun.l.google.com:19302";
    config.servers.push_back(server);

//    rtc::scoped_refptr<webrtc::PeerConnectionInterface> peer_connection_ =
//            peer_connection_factory_->CreatePeerConnection(
//                    config,
//                    nullptr,
//                    nullptr
//                    E);



}