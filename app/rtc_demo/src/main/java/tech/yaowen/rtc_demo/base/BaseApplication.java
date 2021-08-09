package tech.yaowen.rtc_demo.base;

import static tech.yaowen.signaling.HttpsUtil.ignoreSSLHandshake;

import android.app.Application;


public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ignoreSSLHandshake();
    }
}
