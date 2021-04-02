package tech.yaowen.rtc_demo.base;

import android.app.Application;

import static tech.yaowen.rtc_demo.lib.HttpsUtil.ignoreSSLHandshake;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ignoreSSLHandshake();
    }
}
