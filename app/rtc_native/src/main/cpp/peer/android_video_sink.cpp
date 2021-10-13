//
// Created by Albert on 2021/7/21.
//

#include <api/video/video_rotation.h>
#include <android/native_window.h>
#include <android/native_window.h>
#include <base/esUtil.h>
#include <utils/camera_utils.h>

#include "android_video_sink.h"
#include "android_video_frame_buffer.h"

using webrtc::VideoRotation;
namespace rtc_demo {

    AndroidVideoSink::AndroidVideoSink(ANativeWindow *window) : window_(window) {
    }


    AndroidVideoSink::~AndroidVideoSink() {
        window_ = nullptr;
    }


    void AndroidVideoSink::OnFrame(const VideoFrame &frame) {
        if (window_ == nullptr) {
            return;
        }

        ANativeWindow_acquire(window_);
        ANativeWindow_Buffer buf;
        if (ANativeWindow_lock(window_, &buf, nullptr) < 0) {
            return;
        }
        auto buffer = frame.video_frame_buffer();
        rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf = buffer->ToI420();
        switch (VideoRotation::kVideoRotation_0) {
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
        frame_buf.release();
        ANativeWindow_unlockAndPost(window_);
        ANativeWindow_release(window_);
    }


    void AndroidVideoSink::PresentImage(ANativeWindow_Buffer *buf,
                                        const rtc::scoped_refptr<webrtc::I420BufferInterface> &frame_buf) {
        int32_t yStride, uvStride;
        const uint8_t *yPixel, *uPixel, *vPixel;
        int32_t yLen, uLen, vLen;
        yPixel = frame_buf->DataY();
        uPixel = frame_buf->DataU();
        vPixel = frame_buf->DataV();

        yStride = frame_buf->StrideY();
        uvStride = frame_buf->StrideU();

        int32_t height = MIN(buf->height, frame_buf->height());
        int32_t width = MIN(buf->width, frame_buf->height());

        auto *out = static_cast<uint32_t *>(buf->bits);
        for (int32_t y = 0; y < height; y++) {
            const uint8_t *pY = yPixel + yStride * y;

            int32_t uv_row_start = uvStride * (y >> 1); // 行长是 Y 的一半
            const uint8_t *pU = uPixel + uv_row_start; // 裁剪的偏移量也是 Y 的一半
            const uint8_t *pV = vPixel + uv_row_start;

            for (int32_t x = 0; x < width; x++) {
                const int32_t uv_offset = (x >> 1);
                out[x] = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
            }
            out += buf->stride;
        }
    }


    void AndroidVideoSink::PresentImage90(ANativeWindow_Buffer *buf,
                                          rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }


    void AndroidVideoSink::PresentImage180(ANativeWindow_Buffer *buf,
                                           rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }


    void AndroidVideoSink::PresentImage270(ANativeWindow_Buffer *buf,
                                           rtc::scoped_refptr<webrtc::I420BufferInterface> frame_buf) {

    }
}