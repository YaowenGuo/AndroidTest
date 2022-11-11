//
// Created by Lim on 2022/11/11.
//

#include <media/NdkImage.h>
#include "window_monitor.h"
#include "utils/native_debug.h"
#include "peer/my_rtc_engine.h"

WindowMonitor *WindowMonitor::p_window_monitor = nullptr;


WindowMonitor *WindowMonitor::GetInstance() {
    if (p_window_monitor == nullptr) {
        p_window_monitor = new WindowMonitor();
        LOGE("WindowMonitor：create");
    }
    return p_window_monitor;
}


WindowMonitor::WindowMonitor()
        : app_(nullptr),
          rotation_(0),
          window_width_(0),
          window_height_(0),
          window_format_(0) {
//    memset(&savedNativeWinRes_, 0, sizeof(savedNativeWinRes_));
}


WindowMonitor::~WindowMonitor() {
    p_window_monitor = nullptr;
}


void WindowMonitor::SetAndroidApp(android_app *app) {
    ASSERT(app, "android_app is nullptr");
    app_ = app;
}


android_app *WindowMonitor::App() {
    ASSERT(app_, "android_app is nullptr");
    return app_;
}


void WindowMonitor::OnInitWindow() {
    if (app_->window == nullptr) return;
    rotation_ = GetDisplayRotation();
    window_width_ = ANativeWindow_getWidth(app_->window);
    window_height_ = ANativeWindow_getHeight(app_->window);
    window_format_ = ANativeWindow_getFormat(app_->window);
    LOGE("WindowMonitor：OnInitWindow: rot: %d, width: %d, height: %d, format: %d",
         rotation_, window_width_, window_height_, window_format_);
    // AHARDWAREBUFFER_FORMAT_Y8Cb8Cr8_420 is default format.
    auto state = ANativeWindow_setBuffersGeometry(
            app_->window, window_width_, window_height_, AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM
    );
    ASSERT(state == 0, "ANativeWindow_setBuffersGeometry error : %d", state);
}


void WindowMonitor::OnTermWindow() {
    LOGE("WindowMonitor：OnTermWindow");
//    cameraReady_ = false;
//    DeleteCamera();
    ANativeWindow_setBuffersGeometry(app_->window, window_width_, window_height_, window_format_);
}


void WindowMonitor::OnStart() {
    LOGE("WindowMonitor：OnStart");

}


void WindowMonitor::OnStop() {
    LOGE("WindowMonitor：OnStop");

}


void WindowMonitor::OnDestroy() {
    LOGE("WindowMonitor：OnDestroy");
    ASSERT(p_window_monitor, "AppEngine has not initialized or already released");
    // 如何区分屏幕旋转的销毁而不必释放？
    // delete WindowMonitor::p_window_monitor;
}


void WindowMonitor::OnPause() {
    LOGE("WindowMonitor：OnPause");

}


void WindowMonitor::OnResume() {
    LOGE("WindowMonitor：OnResume");

}


void WindowMonitor::DrawFrame() {
    return;
    if (pLiveObj == nullptr) return;
    auto camera = pLiveObj->GetCamera().lock();
    if (camera == nullptr) return;
    auto yuv_reader = camera->GetYuvReader().lock();
    if (yuv_reader == nullptr) return;
    AImage *image = yuv_reader->GetNextImage();
    if (!image) {
        return;
    }

    ANativeWindow_acquire(app_->window);
    ANativeWindow_Buffer buf;
    if (ANativeWindow_lock(app_->window, &buf, nullptr) == 0) {
        yuv_reader->DisplayImage(&buf, image);
        ANativeWindow_unlockAndPost(app_->window);
    }
//    yuv_reader->DeleteImage(image);
//    ANativeWindow_unlockAndPost(app_->window);
    ANativeWindow_release(app_->window);
}


void WindowMonitor::OnWindowSizeChange() {
    LOGE("WindowMonitor：OnWindowSizeChange");

}


void WindowMonitor::OnAppConfigChange() {
    LOGE("WindowMonitor：OnAppConfigChange");
    int newRotation = GetDisplayRotation();
    if (newRotation != rotation_) {
        rotation_ = newRotation;
    }
}


/**
 * Retrieve current rotation from Java side
 *
 * @return current rotation angle
 */
int WindowMonitor::GetDisplayRotation() {
    ASSERT(app_, "Application is not initialized");
    auto newOrientation = AConfiguration_getOrientation(app_->config);
    LOGE("WindowMonitor：GetDisplayRotation: newOrientation: %d", newOrientation);
//    auto config  = ;
//
//    JNIEnv *env;
//    activity->getDi
//    int status = activity->vm->GetEnv((void **) &env, JNI_VERSION_1_6);
//    bool isAttached = false;
//    if (status < 0) {
//        if (activity->vm->AttachCurrentThread(&env, NULL)){
//            return 0;
//        }
//        isAttached = true;
//    }
//
//    jobject activityObj = env->NewGlobalRef(activity->clazz);
//    jclass clz = env->GetObjectClass(activityObj);
//    jint newOrientation = env->CallIntMethod(
//            activityObj, env->GetMethodID(clz, "getRotationDegree", "()I"));
//    env->DeleteGlobalRef(activityObj);
//
//    if (isAttached) {
//        activity->vm->DetachCurrentThread();
//    }
    return newOrientation;
}


int32_t WindowMonitor::WindowWidth() {
    return window_width_;
}


int32_t WindowMonitor::WindowHeight() {
    return window_height_;
}
