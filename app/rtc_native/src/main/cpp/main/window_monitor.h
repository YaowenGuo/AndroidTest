//
// Created by Lim on 2022/11/11.
//

#ifndef ANDROIDTEST_WINDOW_MONITOR_H
#define ANDROIDTEST_WINDOW_MONITOR_H

#include <android_native_app_glue.h>

class WindowMonitor {
public:

    static WindowMonitor *GetInstance();

    void SetAndroidApp(android_app *app);

    android_app* App();
    /**
     * Handle Android System APP_CMD_INIT_WINDOW message
     *   Request camera persmission from Java side
     *   Create camera object if camera has been granted
     */
    void OnInitWindow();

    /**
     * Handle APP_CMD_TEMR_WINDOW
     */
    void OnTermWindow();


    void OnAppConfigChange();


    void DrawFrame();


    void OnStart();


    void OnStop();


    void OnDestroy();


    void OnWindowSizeChange();


    void OnResume();


    void OnPause();

     ~WindowMonitor();

    int32_t GetDisplayRotation();

    int32_t WindowWidth();

    int32_t WindowHeight();

private:
    explicit WindowMonitor();

    static WindowMonitor *p_window_monitor;
    android_app *app_;
    int32_t rotation_;
    int32_t window_width_;
    int32_t window_height_;
    int32_t window_format_;
};


#endif //ANDROIDTEST_WINDOW_MONITOR_H
