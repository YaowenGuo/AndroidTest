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
#include <camera/camera_engine.h>


#define _LIBCPP_NAMESPACE _LIBCPP_CONCAT(__,_LIBCPP_ABI_VERSION)
// Names used for a SessionDescription JSON object.
const char kSessionDescriptionTypeName[] = "type";
const char kSessionDescriptionSdpName[] = "sdp";


class DummySetSessionDescriptionObserver
        : public webrtc::SetSessionDescriptionObserver {
public:
    static DummySetSessionDescriptionObserver *Create() {
        return new rtc::RefCountedObject<DummySetSessionDescriptionObserver>();
    }


    virtual void OnSuccess() { RTC_LOG(INFO) << __FUNCTION__; }


    virtual void OnFailure(webrtc::RTCError error) {
        RTC_LOG(INFO) << __FUNCTION__ << " " << ToString(error.type()) << ": "
                      << error.message();
    }
};


Live::Live(JNIEnv *jni, jobject context) {
    /**
     * 1. 初始化
     */
    JavaVM *jvm = nullptr;
    jni->GetJavaVM(&jvm);

    webrtc::JVM::Initialize(jvm, context);
    // InitFieldTrialsFromString stores the char*, so the char array must
    // outlive the application.
    const std::string field_trials /*absl::GetFlag(FLAGS_force_fieldtrials)*/;
    webrtc::field_trial::InitFieldTrialsFromString(field_trials.c_str());
//    rtc::PhysicalSocketServer socket = rtc::PhysicalSocketServer();
//    rtc::AutoSocketServerThread thread(&socket);
    rtc::InitializeSSL();
    signaling_ = new rtc_demo::SignalingClient(this);

    // ------------------------
}


void Live::joinRoom(const string &room) {
//    signaling_->join(room);
    onCreateRoom();
}


Live::~Live() {
    delete signaling_;
}


void Live::createEngine() {
    /**
     * 2. 创建 PeerConnectionFactory, 因为 Webrtc 可以同时进行多个连接，以创建多个 PeerConnection (PC).
     */
//    rtc::ThreadManager::Instance()->WrapCurrentThread();
//
//    std::unique_ptr<rtc::Thread> network_thread =
//            rtc::Thread::CreateWithSocketServer();
//    network_thread->SetName("network_thread", nullptr);
//    RTC_CHECK(network_thread->Start()) << "Failed to start thread";
//
//    std::unique_ptr<rtc::Thread> worker_thread = rtc::Thread::Create();
//    worker_thread->SetName("worker_thread", nullptr);
//    RTC_CHECK(worker_thread->Start()) << "Failed to start thread";
//
//
//    std::unique_ptr<rtc::Thread> signaling_thread = rtc::Thread::Create();
//    signaling_thread->SetName("signaling_thread", NULL);
//    RTC_CHECK(signaling_thread->Start()) << "Failed to start thread";

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
    webrtc::PeerConnectionInterface::IceServer server;
    server.uri = "stun:stun.l.google.com:19302";
    config.servers.push_back(server);

    peer_connection_ = peer_connection_factory_->CreatePeerConnection(
            config,
            nullptr,
            nullptr,
            this
    );
}


rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource>
Live::AddTracks(rtc::VideoSinkInterface<VideoFrame> *videoSink) {
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
    RTC_LOG(INFO) << __FUNCTION__ << " " << candidate->sdp_mline_index();

    Json::StyledWriter writer;
    Json::Value jmessage;

    jmessage["type"] = "candidate";
    jmessage["label"] = candidate->sdp_mline_index();
    jmessage["id"] = candidate->sdp_mid();
    jmessage[kCandidateSdpMidName] = candidate->sdp_mid();
    jmessage[kCandidateSdpMlineIndexName] = candidate->sdp_mline_index();
    std::string sdp;
    if (!candidate->ToString(&sdp)) {
        RTC_LOG(LS_ERROR) << "Failed to serialize candidate";
        return;
    }
    jmessage[kCandidateSdpName] = sdp;
    signaling_->SendIceCandidate(writer.write(jmessage));
}


//「***************** CreateSessionDescriptionObserver *******************
void Live::OnSuccess(SessionDescriptionInterface *desc) {
    // 可以在设置之前，对 SDP 做一些排序等操作，以设置某些编解码的优先级。
    peer_connection_->SetLocalDescription(std::unique_ptr<SessionDescriptionInterface>(desc), this);
    // 发起者没有收到 remote session, 应该为空。
    // 发起者创建 session 成功后要发送到远端。
    std::string sdp;
    desc->ToString(&sdp);

    Json::StyledWriter writer;
    Json::Value jmessage;
    jmessage[kSessionDescriptionTypeName] =
            webrtc::SdpTypeToString(desc->GetType());
    jmessage[kSessionDescriptionSdpName] = sdp;
    signaling_->SendSessionDescription(writer.write(jmessage));

}


void Live::OnFailure(RTCError error) {
    RTC_LOG(LS_ERROR) << "Failed to create SDP: " << error.message();
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



void Live::InitLive() {
    //    LOGE("webrtc_albert: %s", std::this_thread::get_id());
    createEngine(); // PeerConnectionFactory + PeerConnection.
    auto videoSink = new rtc_demo::AndroidVideoSink(GetAppEngine()->app_->window);
//    videoTrack = AddTracks(videoSink); // add audio and video track.
    GetAppEngine()->CreateCamera(videoTrack);
}


//「***************** SocketCallbackInterface *******************
void Live::onCreateRoom() {
    InitLive();
    connectToPeer(nullptr);
}


void Live::onJoinedRoom() {
    InitLive();
}


void Live::onPeerJoined() {

}


void Live::onPeerLeave(const string &msg) {
}


void Live::onSDPReceived(const string &message) {
    Json::Reader reader;
    Json::Value jmessage;
    if (!reader.parse(message, jmessage)) {
        RTC_LOG(WARNING) << "Received unknown message. " << message;
        return;
    }
    std::string type_str;
    std::string json_object;

    rtc::GetStringFromJsonObject(jmessage, kSessionDescriptionTypeName,
                                 &type_str);
    if (!type_str.empty()) {
        absl::optional<webrtc::SdpType> type_maybe =
                webrtc::SdpTypeFromString(type_str);
        if (!type_maybe) {
            RTC_LOG(LS_ERROR) << "Unknown SDP type: " << type_str;
            return;
        }
        webrtc::SdpType type = *type_maybe;
        std::string sdp;
        if (!rtc::GetStringFromJsonObject(jmessage, kSessionDescriptionSdpName,
                                          &sdp)) {
            RTC_LOG(WARNING) << "Can't parse received session description message.";
            return;
        }

        webrtc::SdpParseError error;
        std::unique_ptr<webrtc::SessionDescriptionInterface> session_description =
                webrtc::CreateSessionDescription(type, sdp, &error);
        if (!session_description) {
            RTC_LOG(WARNING) << "Can't parse received session description message. "
                                "SdpParseError was: "
                             << error.description;
            return;
        }
        RTC_LOG(INFO) << " Received session description :" << message;
        peer_connection_->SetRemoteDescription(
                DummySetSessionDescriptionObserver::Create(),
                session_description.release());
        if (type == webrtc::SdpType::kOffer) {
            peer_connection_->CreateAnswer(
                    this, webrtc::PeerConnectionInterface::RTCOfferAnswerOptions());
        }
    }
}


void Live::onIceCandidateReceived(const string &message) {
    Json::Reader reader;
    Json::Value jmessage;
    if (!reader.parse(message, jmessage)) {
        RTC_LOG(WARNING) << "Received unknown message. " << message;
        return;
    }

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
    RTC_LOG(INFO) << " Received candidate :" << message;
}
// L***************** SocketCallbackInterface *******************