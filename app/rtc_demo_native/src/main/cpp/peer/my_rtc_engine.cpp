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
#include "my_rtc_engine.h"
#include "base/esUtil.h"
#include "android_video_track_source.h"
#include <api/video_codecs/builtin_video_decoder_factory.h>

#include <modules/video_capture/video_capture_factory.h>
#include <rtc_base/strings/json.h>


#define _LIBCPP_NAMESPACE _LIBCPP_CONCAT(__,_LIBCPP_ABI_VERSION)

Live::Live(JNIEnv *jni, jobject context) {
    /**
     * 1. 初始化
     */
    // JNIEnv* env; (initialized somewhere else)
    JavaVM* jvm = nullptr;
    jni->GetJavaVM(&jvm);

    webrtc::JVM::Initialize(jvm, context);
    // InitFieldTrialsFromString stores the char*, so the char array must
    // outlive the application.
    const std::string field_trials /*absl::GetFlag(FLAGS_force_fieldtrials)*/;
    webrtc::field_trial::InitFieldTrialsFromString(field_trials.c_str());
//    rtc::PhysicalSocketServer socket = rtc::PhysicalSocketServer();
//    rtc::AutoSocketServerThread thread(&socket);
    rtc::InitializeSSL();
}

void Live::createEngine() {
    /**
     * 2. 创建 PeerConnectionFactory, 因为 Webrtc 可以同时进行多个连接，以创建多个 PeerConnection (PC).
     */
    peer_connection_factory_ = webrtc::CreatePeerConnectionFactory(
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
//    webrtc::PeerConnectionInterface::IceServer server;
//    server.uri = "stun:stun.l.google.com:19302";
//    config.servers.push_back(server);

    peer_connection_ = peer_connection_factory_->CreatePeerConnection(
            config,
            nullptr,
            nullptr,
            this
    );
}


rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource> Live::AddTracks(rtc::VideoSinkInterface<VideoFrame>* videoSink) {
    if (!peer_connection_->GetSenders().empty()) {
        return nullptr;  // Already added tracks.
    }
    rtc::scoped_refptr<AudioSourceInterface> audioSource =
            peer_connection_factory_->CreateAudioSource(cricket::AudioOptions());
    rtc::scoped_refptr<webrtc::AudioTrackInterface> audio_track =
            peer_connection_factory_->CreateAudioTrack(
                    kAudioLabel,
                    audioSource
            );

    auto result_or_error = peer_connection_->AddTrack(audio_track, {kStreamId});
    if (!result_or_error.ok()) {
        RTC_LOG(LS_ERROR) << "Failed to add audio track to PeerConnection: "
                          << result_or_error.error().message();
    }

    // video source
    rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource> video_source = new rtc::RefCountedObject<rtc_demo::AndroidVideoTrackSource>(
            peer_connection_->signaling_thread(), false, false);

    // track
    rtc::scoped_refptr<webrtc::VideoTrackInterface> video_track =
            peer_connection_factory_->CreateVideoTrack(kVideoLabel, video_source);
    // add video sink
    video_track->AddOrUpdateSink(videoSink,
//            reinterpret_cast<rtc::VideoSinkInterface<VideoFrame>*>(j_native_sink),
            rtc::VideoSinkWants());

    result_or_error = peer_connection_->AddTrack(video_track, {kStreamId});
    if (!result_or_error.ok()) {
        RTC_LOG(LS_ERROR) << "Failed to add video track to PeerConnection: "
                          << result_or_error.error().message();
    }

    return video_source;
//    AddOrUpdateSink(this, rtc::VideoSinkWants()
//    main_wnd_->SwitchToStreamingUI();
}

void Live::connectToPeer(SessionDescriptionInterface *desc) {
    if (desc == nullptr) {
        peer_connection_->CreateOffer(this,
                                      webrtc::PeerConnectionInterface::RTCOfferAnswerOptions());
    } else {
        setRemoteDescription(desc);
        peer_connection_->CreateAnswer(this,
                                       webrtc::PeerConnectionInterface::RTCOfferAnswerOptions());
    }
}

void Live::setRemoteDescription(SessionDescriptionInterface *desc) {
    peer_connection_->SetRemoteDescription(std::unique_ptr<SessionDescriptionInterface>(desc),
                                           this);

}

void Live::addIce(const Json::Value jmessage) {
    std::string sdp_mid;
    int sdp_mlineindex = 0;
    std::string sdp;
    if (!rtc::GetStringFromJsonObject(jmessage, kCandidateSdpMidName,
                                      &sdp_mid) ||
        !rtc::GetIntFromJsonObject(jmessage, kCandidateSdpMlineIndexName,
                                   &sdp_mlineindex) ||
        !rtc::GetStringFromJsonObject(jmessage, kCandidateSdpName, &sdp)) {
        RTC_LOG(WARNING) << "Can't parse received message.";
        return;
    }
    webrtc::SdpParseError error;
    std::unique_ptr<webrtc::IceCandidateInterface> candidate(
            webrtc::CreateIceCandidate(sdp_mid, sdp_mlineindex, sdp, &error));
    if (!candidate.get()) {
        RTC_LOG(WARNING) << "Can't parse received candidate message. "
                            "SdpParseError was: "
                         << error.description;
        return;
    }
    if (!peer_connection_->AddIceCandidate(candidate.get())) {
        RTC_LOG(WARNING) << "Failed to apply the received candidate";
        return;
    }
}

void Live::OnIceCandidate(const IceCandidateInterface *candidate) {
    // TODO send ice to peer.
}

//「***************** CreateSessionDescriptionObserver *******************
void Live::OnSuccess(SessionDescriptionInterface *desc) {
    peer_connection_->SetLocalDescription(std::unique_ptr<SessionDescriptionInterface>(desc), this);
    // 发起者没有收到 remote session, 应该为空。
    // 发起者创建 session 成功后要发送到远端。
    // TODO send session to peer.

}


void Live::OnFailure(RTCError error) {

}
// L***************** CreateSessionDescriptionObserver *******************


//「***************** SetLocalDescriptionObserverInterface *******************
void Live::OnSetLocalDescriptionComplete(RTCError error) {

}
// L***************** SetLocalDescriptionObserverInterface *******************


//「***************** SetRemoteDescriptionObserverInterface *******************
void Live::OnSetRemoteDescriptionComplete(RTCError error) {

}
// L***************** SetRemoteDescriptionObserverInterface *******************