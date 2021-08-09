package tech.yaowen.rtc_demo_native.signaling;

import android.content.Context;

import androidx.annotation.Nullable;

import org.json.JSONObject;


public class SignalingClient extends tech.yaowen.signaling.SignalingClient implements tech.yaowen.signaling.SignalingClient.Callback {
    protected static SignalingClient instance;
    public static SignalingClient getInstance(Context context) {
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

    private SignalingClient(Context context) {
        super(context);
    }

    @Override
    public void onCreateRoom() {
        joinRoom(getContext());
    }

    @Override
    public void onJoinedRoom() {
        joinRoom(getContext());
    }

    @Override
    public void onPeerJoined() {
    }

    @Override
    public void onPeerLeave(@Nullable String msg) {

    }

    @Override
    public void onOfferReceived(@Nullable JSONObject data) {
        answer(getContext(), data.toString());
    }

    @Override
    public void onAnswerReceived(@Nullable JSONObject data) {

    }

    @Override
    public void onIceCandidateReceived(@Nullable JSONObject data) {

    }
    private native static void joinRoom(Context context);
    private native static void answer(Context context, String offer);
}
