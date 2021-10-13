package tech.yaowen.rtc_native.signaling;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import org.json.JSONObject;


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

    private native static void joinRoom(Context context, SignalingClient signaling);

    private native static void answer(Context context, SignalingClient signaling, String offer);
}
