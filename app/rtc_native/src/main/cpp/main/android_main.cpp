/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <android/native_window.h>
#include <android_native_app_glue.h>
#include "utils/native_debug.h"
#include "testthread/test_thread.h"
#include "window_monitor.h"

/**
 * Teamplate function for NativeActivity derived applications
 *   Create/Delete camera object with
 *   INIT_WINDOW/TERM_WINDOW command, ignoring other event.
 */
void ProcessAndroidCmd(struct android_app *app, int32_t cmd) {
    auto monitor = reinterpret_cast<WindowMonitor *>(app->userData);
    switch (cmd) {
        case APP_CMD_START: // 1
            monitor->OnStart();
            break;
        case APP_CMD_RESUME: // 2
            monitor->OnResume();
            break;
        case APP_CMD_INIT_WINDOW: // 3 INIT_WINDOW 在 RESUME 之后
            monitor->OnInitWindow();
            break;
        case APP_CMD_WINDOW_RESIZED: // 4 接着有一个 WINDOW_RESIZED
            monitor->OnWindowSizeChange();
            break;
        case APP_CMD_PAUSE: // 5
            monitor->OnPause();
            break;
        case APP_CMD_TERM_WINDOW: // 6 TERM_WINDOW 和 INIT_WINDOW 并不对称
            monitor->OnTermWindow();
            break;
        case APP_CMD_STOP: // 7
            monitor->OnStop();
            break;
        case APP_CMD_DESTROY: // 8
            monitor->OnDestroy();
            break;
        case APP_CMD_CONFIG_CHANGED: // ?
            monitor->OnAppConfigChange();
            break;
        default:
            break;
    }
}


// 旋转屏幕会重新走一边，相当于 OnCreate 的回调。
extern "C" void android_main(struct android_app *app) {
    auto window_monitor = WindowMonitor::GetInstance();
    window_monitor->SetAndroidApp(app);
    app->userData = reinterpret_cast<void *>(window_monitor);
    app->onAppCmd = ProcessAndroidCmd;
    // loop waiting for stuff to do.
    while (true) {
        // Read all pending events.
        int events;
        struct android_poll_source *source;

        while (ALooper_pollAll(0, nullptr, &events, (void **) &source) >= 0) {
            // Process this event.
            if (source != nullptr) {
                source->process(app, source);
            }

            // Check if we are exiting.
            // 在 APP_CMD_DESTROY 之后会成立
            if (app->destroyRequested != 0) {
                LOGI("CameraEngine thread destroy requested!");
                return;
            }
        }
        WindowMonitor::GetInstance()->DrawFrame();
    }
}
