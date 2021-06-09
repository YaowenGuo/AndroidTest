/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#include "sdk/android/src/jni/video_frame.h"

#include <memory>

#include "rtc_base/logging.h"
#include "rtc_base/ref_counted_object.h"
#include "rtc_base/time_utils.h"
#include "sdk/android/src/jni/jni_helpers.h"
#include "sdk/android/src/jni/wrapped_native_i420_buffer.h"
#include "android_video_track_source.h"
#include "android_video_frame_buffer.h"

namespace rtc_demo {
    namespace {
        class AndroidVideoI420Buffer : public webrtc::I420BufferInterface {
        public:
        protected:
            // Should not be called directly. Adopts the buffer. Use Adopt() instead for
            // clarity.
            AndroidVideoI420Buffer(
                    int width,
                    int height,
                    const AImage *);

            ~AndroidVideoI420Buffer() override;

        private:
            const uint8_t *DataY() const override { return data_y_; }

            const uint8_t *DataU() const override { return data_u_; }

            const uint8_t *DataV() const override { return data_v_; }

            int StrideY() const override { return stride_y_; }

            int StrideU() const override { return stride_u_; }

            int StrideV() const override { return stride_v_; }

            int width() const override { return width_; }

            int height() const override { return height_; }

            const int width_;
            const int height_;
            const AImage *image_;
            const uint8_t *data_y_;
            const uint8_t *data_u_;
            const uint8_t *data_v_;
            int stride_y_;
            int stride_u_;
            int stride_v_;
        };

        AndroidVideoI420Buffer::AndroidVideoI420Buffer(
                int width,
                int height,
                const AImage *image
        ) : width_(width),
            height_(height),
            image_(image) {
            uint8_t *yPixel, *uPixel, *vPixel;
            int32_t yLen, uLen, vLen;
            AImage_getPlaneRowStride(image, 0, &stride_y_);
            AImage_getPlaneRowStride(image, 1, &stride_u_);
            AImage_getPlaneRowStride(image, 2, &stride_v_);

            AImage_getPlaneData(image, 0, &yPixel, &yLen);
            AImage_getPlaneData(image, 1, &vPixel, &vLen);
            AImage_getPlaneData(image, 2, &uPixel, &uLen);
            data_y_ = yPixel;
            data_u_ = yPixel;
            data_v_ = vPixel;
        }

        AndroidVideoI420Buffer::~AndroidVideoI420Buffer() = default;
    }  // namespace


    rtc::scoped_refptr<AndroidVideoBuffer> AndroidVideoBuffer::Create(AImage *image) {
        return rtc::make_ref_counted<AndroidVideoBuffer>(image);
    }


    AndroidVideoBuffer::AndroidVideoBuffer(AImage *image) : image_(image){
        AImage_getWidth(image, &width_);
        AImage_getHeight(image, &height_);
    }

    AndroidVideoBuffer::~AndroidVideoBuffer() = default;

    rtc::scoped_refptr<webrtc::VideoFrameBuffer> AndroidVideoBuffer::CropAndScale(
            int crop_x,
            int crop_y,
            int crop_width,
            int crop_height,
            int scale_width,
            int scale_height) {
        return nullptr;
    }

    AndroidVideoBuffer::Type AndroidVideoBuffer::type() const {
        return Type::kNative;
    }

    int AndroidVideoBuffer::width() const {
        return width_;
    }

    int AndroidVideoBuffer::height() const {
        return height_;
    }

    rtc::scoped_refptr<webrtc::I420BufferInterface> AndroidVideoBuffer::ToI420() {
        // We don't need to retain the buffer because toI420 returns a new object that
        // we are assumed to take the ownership of.
        return rtc::make_ref_counted<AndroidVideoI420Buffer>(width_, height_, image_);
    }

}  // namespace webrtc
