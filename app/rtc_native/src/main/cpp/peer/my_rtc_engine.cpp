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
#include <rtc_base/task_queue_gcd.h>
#include <api/rtc_event_log/rtc_event_log_factory.h>
#include <media/engine/webrtc_media_engine.h>
#include <api/task_queue/default_task_queue_factory.h>
#include "utils/jvm.h"

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


Live::Live(JNIEnv *jni, jobject context, rtc_demo::SignalingClientWrapper *signaling)
: peer_connection_factory_(nullptr), peer_connection_(nullptr) {
    /**
     * 1. 初始化
     */
    rtc::InitializeSSL();

    JavaVM *jvm = nullptr;
    jni->GetJavaVM(&jvm);

    webrtc::JVM::Initialize(jvm, context);
    // InitFieldTrialsFromString stores the char*, so the char array must
    // outlive the application.
    const std::string field_trials /*absl::GetFlag(FLAGS_force_fieldtrials)*/;
    webrtc::field_trial::InitFieldTrialsFromString(field_trials.c_str());
//    rtc::PhysicalSocketServer socket = rtc::PhysicalSocketServer();
//    rtc::AutoSocketServerThread thread(&socket);
    signaling_ = signaling;

    // ------------------------
}


Live::~Live() {
    delete signaling_;
}


// One-off message handler that calls the Java method on the specified Java
// object before deleting itself.
class JavaAsyncCallback : public rtc::MessageHandler {
public:
    void OnMessage(rtc::Message *) override {
        jni::AttachCurrentThreadIfNeeded();
        delete this;
    }
};


// Post a message on the given thread that will call the Java method on the
// given Java object.
void PostThreadAttachTask(rtc::Thread *queue, const rtc::Location &posted_from) {
    queue->Post(posted_from, new JavaAsyncCallback());
}


void Live::createEngine() {
    /**
     * 2. 创建 PeerConnectionFactory, 因为 Webrtc 可以同时进行多个连接，以创建多个 PeerConnection (PC).
     */
    rtc::ThreadManager::Instance()->WrapCurrentThread();

    std::unique_ptr<rtc::Thread> network_thread = rtc::Thread::CreateWithSocketServer();
    network_thread->SetName("network_thread", nullptr);
    RTC_CHECK(network_thread->Start()) << "Failed to start thread";

    std::unique_ptr<rtc::Thread> worker_thread = rtc::Thread::Create();
    worker_thread->SetName("worker_thread", nullptr);
    RTC_CHECK(worker_thread->Start()) << "Failed to start thread";

    std::unique_ptr<rtc::Thread> signaling_thread = rtc::Thread::Create();
    signaling_thread->SetName("signaling_thread", NULL);
    RTC_CHECK(signaling_thread->Start()) << "Failed to start thread";

    // 不要使用 get(). 并没有转移所有权，会导致 unique_ptr 在函数退出时释放对象。
    auto network_p = network_thread.release();
    auto worker_p = worker_thread.release();
    auto signaling_p = signaling_thread.release();
    // 线程启动后 attach Jni.
    PostThreadAttachTask(network_p, RTC_FROM_HERE);
    PostThreadAttachTask(worker_p, RTC_FROM_HERE);
    PostThreadAttachTask(signaling_p, RTC_FROM_HERE);
    peer_connection_factory_ = webrtc::CreatePeerConnectionFactory(
            network_p /* network_thread */, worker_p /* worker_thread */,
            signaling_p /* signaling_thread */, nullptr /* default_adm */,
            webrtc::CreateBuiltinAudioEncoderFactory(),
            webrtc::CreateBuiltinAudioDecoderFactory(),
            webrtc::CreateBuiltinVideoEncoderFactory(),
            webrtc::CreateBuiltinVideoDecoderFactory(),
            nullptr /* audio_mixer */,
            nullptr /* audio_processing */
    );
    CreatePeerConnection(false);
    // video source
    rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource> video_source =
            new rtc::RefCountedObject<rtc_demo::AndroidVideoTrackSource>(
                    peer_connection_->signaling_thread(), false, false
            );

    // add audio and video track.
    rtc::scoped_refptr<webrtc::VideoTrackInterface> video_track = AddTracks(video_source);

    // add video sink， 用于本地显示。
    auto videoSink = new rtc_demo::AndroidVideoSink(GetAppEngine()->app_->window);
    video_track->AddOrUpdateSink(videoSink, rtc::VideoSinkWants());

    ImageReader *imageReader = GetAppEngine()->CreateCamera();
    if (imageReader) {
        imageReader->SetVideoSource(video_source);
    } else {
        LOGW("image reader is null.");
    }
}


bool Live::CreatePeerConnection(bool dtls) {
    RTC_DCHECK(peer_connection_factory_);
    RTC_DCHECK(!peer_connection_);
    /**
       * 3. 创建 PC
       */
    webrtc::PeerConnectionInterface::RTCConfiguration config;
    config.sdp_semantics = webrtc::SdpSemantics::kUnifiedPlan;
    config.enable_dtls_srtp = true;
    webrtc::PeerConnectionInterface::IceServer server;
    server.uri = "stun:stun.l.google.com:19302";
    config.servers.push_back(server);
    PeerConnectionDependencies peer_connection_dependencies(this);
    auto result = peer_connection_factory_->CreatePeerConnectionOrError(
            config, std::move(peer_connection_dependencies));

    if (!result.ok()) {
        LOGE("Create peer connection failed: %s", result.error().message());
        return false;
    }
    peer_connection_ = result.value();
    return peer_connection_ != nullptr;
}


void Live::connectToPeer() {
    peer_connection_->CreateOffer(this, offerAnswerOption);
}


rtc::scoped_refptr<webrtc::VideoTrackInterface>
Live::AddTracks(webrtc::VideoTrackSourceInterface *video_source) {
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


    // track
    rtc::scoped_refptr<webrtc::VideoTrackInterface> video_track =
            peer_connection_factory_->CreateVideoTrack(kVideoLabel, video_source);

    result_or_error = peer_connection_->AddTrack(video_track, {kStreamId});
    if (!result_or_error.ok()) {
        RTC_LOG(LS_ERROR) << "Failed to add video track to PeerConnection: "
                          << result_or_error.error().message();
    }

    return video_track;
}


//「***************** PeerConnectionObserver *******************

void Live::OnIceCandidate(const IceCandidateInterface *candidate) {
    THREAD_CURRENT("OnIceCandidate");
    signaling_->SendIceCandidate(candidate);
}


// Triggered when media is received on a new stream from remote peer.
void Live::OnAddStream(rtc::scoped_refptr<MediaStreamInterface> stream) {
    auto tracks = stream->GetVideoTracks();
    if (tracks.size() > 0) {
        auto videoSink = new rtc_demo::AndroidVideoSink(GetAppEngine()->app_->window);
        tracks[0]->AddOrUpdateSink(videoSink, rtc::VideoSinkWants());
    }
}


// Triggered when a remote peer closes a stream.
void Live::OnRemoveStream(rtc::scoped_refptr<MediaStreamInterface> stream) {
    if (stream->GetVideoTracks().size() <= 0) {

    }
}


// Triggered when a remote peer opens a data channel.
void Live::OnDataChannel(rtc::scoped_refptr<DataChannelInterface> data_channel) {

}

// L***************** PeerConnectionObserver *******************



//「***************** CreateSessionDescriptionObserver *******************
void Live::OnSuccess(SessionDescriptionInterface *desc) {
    THREAD_CURRENT("OnSuccess");
    // 可以在设置之前，对 SDP 做一些排序等操作，以设置某些编解码的优先级。
    desc->description();
    peer_connection_->SetLocalDescription(std::move(std::unique_ptr<SessionDescriptionInterface>(desc)),this);
    // 创建 session 成功后要发送到远端。
    signaling_->SendSessionDescription(desc);
}


void Live::OnFailure(RTCError error) {
    THREAD_CURRENT("OnFailure");
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


//「***************** SocketCallbackInterface *******************

void Live::onSDPReceived(const string &message) {
    THREAD_CURRENT("onSDPReceived");
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
    THREAD_CURRENT("onIceCandidateReceived");

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
    webrtc::IceCandidateInterface *candidate = webrtc::CreateIceCandidate(sdp_mid, sdp_mlineindex, sdp, &error);
    if (!candidate) {
        RTC_LOG(WARNING) << "Can't parse received candidate message. "
                            "SdpParseError was: "
                         << error.description;
        return;
    }
    if (!peer_connection_->AddIceCandidate(candidate)) {
        RTC_LOG(WARNING) << "Failed to apply the received candidate";
        return;
    }
    RTC_LOG(INFO) << " Received candidate :" << message;
}
// L***************** SocketCallbackInterface *******************