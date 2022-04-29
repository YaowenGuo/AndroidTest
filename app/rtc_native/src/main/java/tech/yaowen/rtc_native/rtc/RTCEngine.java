package tech.yaowen.rtc_native.rtc;


import static tech.yaowen.signaling.SignalingClient.threadCurrent;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import tech.yaowen.signaling.SignalingClient;


public class RTCEngine implements tech.yaowen.signaling.SignalingClient.Callback {
    private static final int kOffer = 0;    // Description must be treated as an SDP offer.
    private static final int kPrAnswer = 1;  // Description must be treated as an SDP answer, but not a final
    // answer.
    private static final int kAnswer = 2;   // Description must be treated as an SDP final answer, and the
    // offer-answer exchange must be considered complete after
    // receiving this.
    private static final int kRollback = 3; // Resets any pending offers and sets signaling state back to

    protected static RTCEngine instance;
    private Application application;
    private boolean joined;
    private boolean isInitiator;
    private static SignalingClient signaling;


    public static RTCEngine getInstance(Application context) {
        if (instance == null) {
            synchronized (SignalingClient.class) {
                if (instance == null) {
                    instance = new RTCEngine(context);
                    signaling = SignalingClient.get(context);
                    signaling.setCallback(instance);
                }
            }
        }
        return instance;
    }

    protected RTCEngine(Application context) {
        application = context;
    }

    public void joinRoom(String roomName) {
        SignalingClient.threadCurrent("joinRoom");
//        signaling.joinRoom(roomName);
        call(application.getBaseContext(), this);
    }

    @Override
    public void onCreateRoom() {
        joined = true;
        isInitiator = true;
        captureVideoAndVideo();
    }


    @Override
    public void onJoinedRoom() {
        isInitiator = false;
        joined = true;
        captureVideoAndVideo();
    }

    @Override
    public void onPeerJoined() {
    }

    @Override
    public void onPeerReady() {
        // 接收方已经获取到音/视频，可以建立连接了。
        if (isInitiator) {
            call(application.getApplicationContext(), this);
        }
    }

    @Override
    public void onPeerLeave(@Nullable String msg) {

    }

    @Override
    public void onOfferReceived(@NonNull String sd) {
        setRemoteDescription(kOffer, sd);
        answer(sd);
    }

    @Override
    public void onAnswerReceived(@NonNull String sd) {
        setRemoteDescription(kAnswer, sd);
    }

    @Override
    public void onIceCandidateReceived(@NonNull JSONObject data) {
        setIce(data.optString("id"), data.optInt("label"), data.optString("candidate"));
    }


    public void sendIce(IceCandidate iceCandidate) {
        // 通过 Singling 服务器发送 ice。对方接收到后设置。
        JSONObject jo = new JSONObject();
        threadCurrent("sendIce");
        try {
            jo.put("type", "candidate");
            jo.put("label", iceCandidate.sdpMLineIndex);
            jo.put("id", iceCandidate.sdpMid);
            jo.put("candidate", iceCandidate.sdp);
            Log.d("Sending ice", jo.toString());
            signaling.sendMessage(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSd(SessionDescription sdp) {
        // 通过 Singling 服务器发送 offer。对方接收到后设置。
        threadCurrent("sendSd");
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", sdp.type.canonicalForm());
            jo.put("sdp", sdp.description);
            signaling.sendMessage(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private native static void captureVideoAndVideo();

    private native static void call(Context application_context, RTCEngine signaling);

    private native static void answer(String sd);

    private native static void setRemoteDescription(int type, String sdp);

    private native static void setIce(String ice, int sdpMLineIndex, String sdp);
}
