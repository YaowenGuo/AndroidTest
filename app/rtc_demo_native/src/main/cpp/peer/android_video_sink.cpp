//
// Created by Albert on 2021/7/21.
//

#include <api/video/video_rotation.h>
#include <android/native_window.h>
#include <android/native_window.h>

#include "android_video_sink.h"
#include "android_video_frame_buffer.h"

using webrtc::VideoRotation;
namespace rtc_demo {

    AndroidVideoSink::AndroidVideoSink(ANativeWindow *window) : window_(window_) {}

    AndroidVideoSink::~AndroidVideoSink() {
        window_ = nullptr;
    }

    void AndroidVideoSink::OnFrame(const VideoFrame &frame) {
        LOGD("RTC yaowen: %s", "AndroidVideoSink::OnFrame");
        if (window_) {
            return;
        }

        ANativeWindow_acquire(window_);
        ANativeWindow_Buffer buf;
        if (ANativeWindow_lock(window_, &buf, nullptr) < 0) {
            return;
        }

        rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf = frame.video_frame_buffer()->ToI420();

        switch (frame.rotation()) {
            case VideoRotation::kVideoRotation_0:
                PresentImage(&buf, frame_buf);
                break;
            case VideoRotation::kVideoRotation_90:
                PresentImage90(&buf, frame_buf);
                break;
            case VideoRotation::kVideoRotation_180:
                PresentImage180(&buf, frame_buf);
                break;
            case VideoRotation::kVideoRotation_270:
                PresentImage270(&buf, frame_buf);
                break;
            default:
                ASSERT(0, "NOT recognized display rotation: %d", frame.rotation());
        }

        // TODO how to display image ?
        // 1. rotation
        // 2. delete image.
//    yuvReader_->DisplayImage(&buf, image);
        ANativeWindow_unlockAndPost(window_);
        ANativeWindow_release(window_);
    }

    void AndroidVideoSink::PresentImage(ANativeWindow_Buffer *buf, rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }

    void AndroidVideoSink::PresentImage90(ANativeWindow_Buffer *buf, rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }

    void AndroidVideoSink::PresentImage180(ANativeWindow_Buffer *buf, rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }

    void AndroidVideoSink::PresentImage270(ANativeWindow_Buffer *buf, rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }
}