package tech.yaowen.rtc_native.signaling;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;


public class SignalingClient extends tech.yaowen.signaling.SignalingClient implements tech.yaowen.signaling.SignalingClient.Callback {
    protected static SignalingClient instance;
    private Application application;

    public static SignalingClient getInstance(Application context) {
        if (instance == null) {
            synchronized (SignalingClient.class) {
                if (instance == null) {
                    instance = new SignalingClient(context);
                    instance.setCallback(instance);
                }
            }
        }
        return instance;
    }

    private SignalingClient(Application context) {
        super(context);
        application = context;
    }


    @Override
    public void onCreateRoom() {
        joinRoom(application.getApplicationContext(), this);
    }

    @Override
    public void onJoinedRoom() {
//        answer(getContext(), this);
    }

    @Override
    public void onPeerJoined() {
    }

    @Override
    public void onPeerReady() {

    }

    @Override
    public void onPeerLeave(@Nullable String msg) {

    }

    @Override
    public void onOfferReceived(@Nullable JSONObject data) {
        if (data == null) return;
        answer(application, this, data.toString());
    }

    @Override
    public void onAnswerReceived(@Nullable JSONObject data) {

    }

    @Override
    public void onIceCandidateReceived(@Nullable JSONObject data) {

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
            sendMessage(jo);
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
            sendMessage(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private native static void joinRoom(Context context, SignalingClient signaling);

    private native static void answer(Context context, SignalingClient signaling, String offer);


}
