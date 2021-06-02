//
// Created by Albert on 2021/5/25.
//

#ifndef ANDROIDTEST_MY_RTC_ENGINE_H
#define ANDROIDTEST_MY_RTC_ENGINE_H


#include <api/peer_connection_interface.h>
#include <pc/video_track_source.h>
#include <third_party/jsoncpp/source/include/json/value.h>

// InitFieldTrialsFromString stores the char*, so the char array must outlive
// the application.
//const std::string forced_field_trials =
//        absl::GetFlag(FLAGS_force_fieldtrials);
//webrtc::field_trial::InitFieldTrialsFromString(forced_field_trials.c_str());
//
//ABSL_FLAG(
//        std::string,
//        force_fieldtrials,
//        "",
//        "Field trials control experimental features. This flag specifies the field "
//        "trials in effect. E.g. running with "
//        "--force_fieldtrials=WebRTC-FooFeature/Enabled/ "
//        "will assign the group Enabled to field trial WebRTC-FooFeature. Multiple "
//        "trials are separated by \"/\"");

const char kAudioLabel[] = "audio_label";
const char kVideoLabel[] = "video_label";
const char kStreamId[] = "stream_id";

// Names used for a IceCandidate JSON object.
const char kCandidateSdpMidName[] = "sdpMid";
const char kCandidateSdpMlineIndexName[] = "sdpMLineIndex";
const char kCandidateSdpName[] = "candidate";

using namespace webrtc;

class Live : public PeerConnectionObserver, public CreateSessionDescriptionObserver, public SetLocalDescriptionObserverInterface,
             public SetRemoteDescriptionObserverInterface, public rtc::VideoSinkInterface<webrtc::VideoFrame> {
public:
    Live(JNIEnv *jni, jobject context);
    void createEngine();
    void AddTracks(JNIEnv* jni);
    void connectToPeer(SessionDescriptionInterface* desc);
    void setRemoteDescription(SessionDescriptionInterface *desc);
    void addIce(const Json::Value jmessage);

    //「***************** CreateSessionDescriptionObserver *******************
    void OnSuccess(SessionDescriptionInterface* desc) override;
    void OnFailure(RTCError error) override;
    // L***************** CreateSessionDescriptionObserver *******************


    //「***************** SetLocalDescriptionObserverInterface *******************
    void OnSetLocalDescriptionComplete(RTCError error) override;
    // L***************** SetLocalDescriptionObserverInterface *******************


    //「***************** SetRemoteDescriptionObserverInterface *******************
    void OnSetRemoteDescriptionComplete(RTCError error) override;
    // L***************** SetRemoteDescriptionObserverInterface *******************


    //「***************** SetRemoteDescriptionObserverInterface *******************
    void AddRef() const override {};
    rtc::RefCountReleaseStatus Release() const override {};
    // L***************** SetRemoteDescriptionObserverInterface *******************

    //「***************** VideoSinkInterface *******************
    void OnFrame(const VideoFrame& frame) override;
    void OnDiscardedFrame() override;
    // L***************** VideoSinkInterface *******************

    // Triggered when the SignalingState changed.
    void OnSignalingChange(
            PeerConnectionInterface::SignalingState new_state) override {};

    // Triggered when media is received on a new stream from remote peer.
    void OnAddStream(
            rtc::scoped_refptr<MediaStreamInterface> stream) override {};

    // Triggered when a remote peer closes a stream.
    void OnRemoveStream(
            rtc::scoped_refptr<MediaStreamInterface> stream) override {};

    // Triggered when a remote peer opens a data channel.
    void OnDataChannel(
            rtc::scoped_refptr<DataChannelInterface> data_channel) override {}

    // Triggered when renegotiation is needed. For example, an ICE restart
    // has begun.
    // TODO(hbos): Delete in favor of OnNegotiationNeededEvent() when downstream
    // projects have migrated.
    void OnRenegotiationNeeded() override {}

    // Used to fire spec-compliant onnegotiationneeded events, which should only
    // fire when the Operations Chain is empty. The observer is responsible for
    // queuing a task (e.g. Chromium: jump to main thread) to maybe fire the
    // event. The event identified using |event_id| must only fire if
    // PeerConnection::ShouldFireNegotiationNeededEvent() returns true since it is
    // possible for the event to become invalidated by operations subsequently
    // chained.
    void OnNegotiationNeededEvent(
            uint32_t event_id) override {}

    // Called any time the legacy IceConnectionState changes.
    //
    // Note that our ICE states lag behind the standard slightly. The most
    // notable differences include the fact that "failed" occurs after 15
    // seconds, not 30, and this actually represents a combination ICE + DTLS
    // state, so it may be "failed" if DTLS fails while ICE succeeds.
    //
    // TODO(jonasolsson): deprecate and remove this.
    void OnIceConnectionChange(
            PeerConnectionInterface::IceConnectionState new_state) override {}

    // Called any time the standards-compliant IceConnectionState changes.
    void OnStandardizedIceConnectionChange(
            PeerConnectionInterface::IceConnectionState new_state) override {}

    // Called any time the PeerConnectionState changes.
    void OnConnectionChange(
            PeerConnectionInterface::PeerConnectionState new_state) override {}

    // Called any time the IceGatheringState changes.
    void OnIceGatheringChange(
            PeerConnectionInterface::IceGatheringState new_state) override {}

    // A new ICE candidate has been gathered.
    void OnIceCandidate(const IceCandidateInterface *candidate) override;

    // Gathering of an ICE candidate failed.
    // See https://w3c.github.io/webrtc-pc/#event-icecandidateerror
    // |host_candidate| is a stringified socket address.
    void OnIceCandidateError(
            const std::string &host_candidate,
            const std::string &url,
            int error_code,
            const std::string &error_text
    ) override {}

    // Gathering of an ICE candidate failed.
    // See https://w3c.github.io/webrtc-pc/#event-icecandidateerror
    void OnIceCandidateError(
            const std::string &address,
            int port,
            const std::string &url,
            int error_code,
            const std::string &error_text
    ) override {}

    // Ice candidates have been removed.
    // TODO(honghaiz): Make this a pure method when all its subclasses
    // implement it.
    void OnIceCandidatesRemoved(
            const std::vector<cricket::Candidate> &candidates) override {}

    // Called when the ICE connection receiving status changes.
    void OnIceConnectionReceivingChange(
            bool receiving) override {}

    // Called when the selected candidate pair for the ICE connection changes.
    void OnIceSelectedCandidatePairChanged(
            const cricket::CandidatePairChangeEvent &event) override {}

    // This is called when a receiver and its track are created.
    // TODO(zhihuang): Make this pure when all subclasses implement it.
    // Note: This is called with both Plan B and Unified Plan semantics. Unified
    // Plan users should prefer OnTrack, OnAddTrack is only called as backwards
    // compatibility (and is called in the exact same situations as OnTrack).
    void OnAddTrack(
            rtc::scoped_refptr<RtpReceiverInterface> receiver,
            const std::vector<rtc::scoped_refptr<MediaStreamInterface>> &streams) override {}

    // This is called when signaling indicates a transceiver will be receiving
    // media from the remote endpoint. This is fired during a call to
    // SetRemoteDescription. The receiving track can be accessed by:
    // |transceiver->receiver()->track()| and its associated streams by
    // |transceiver->receiver()->streams()|.
    // Note: This will only be called if Unified Plan semantics are specified.
    // This behavior is specified in section 2.2.8.2.5 of the "Set the
    // RTCSessionDescription" algorithm:
    // https://w3c.github.io/webrtc-pc/#set-description
    void OnTrack(rtc::scoped_refptr<RtpTransceiverInterface> transceiver) override {}

    // Called when signaling indicates that media will no longer be received on a
    // track.
    // With Plan B semantics, the given receiver will have been removed from the
    // PeerConnection and the track muted.
    // With Unified Plan semantics, the receiver will remain but the transceiver
    // will have changed direction to either sendonly or inactive.
    // https://w3c.github.io/webrtc-pc/#process-remote-track-removal
    // TODO(hbos,deadbeef): Make pure when all subclasses implement it.
    void OnRemoveTrack(rtc::scoped_refptr<RtpReceiverInterface> receiver) override {}

    // Called when an interesting usage is detected by WebRTC.
    // An appropriate action is to add information about the context of the
    // PeerConnection and write the event to some kind of "interesting events"
    // log function.
    // The heuristics for defining what constitutes "interesting" are
    // implementation-defined.
    void OnInterestingUsage(int usage_pattern) override {}

protected:
    rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> peer_connection_factory_;
    rtc::scoped_refptr<webrtc::PeerConnectionInterface> peer_connection_;

    std::unique_ptr<rtc::Thread> network_thread;
    std::unique_ptr<rtc::Thread> worker_thread;
    std::unique_ptr<rtc::Thread> signaling_thread;
};



#endif //ANDROIDTEST_MY_RTC_ENGINE_H
