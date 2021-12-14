/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#include <sdk/android/src/jni/video_frame.h>
#include <memory>
#include <rtc_base/logging.h>
#include <rtc_base/ref_counted_object.h>
#include <rtc_base/time_utils.h>
#include <sdk/android/src/jni/jni_helpers.h>
#include <sdk/android/src/jni/wrapped_native_i420_buffer.h>
#include <utils/camera_utils.h>
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
            image_(image),
            stride_y_(width),
            stride_u_(width >> 1),
            stride_v_(width >> 1) {
            int32_t yStride, uvStride;
            uint8_t *yPixel, *uPixel, *vPixel;
            int32_t yLen, uLen, vLen;
            AImage_getPlaneRowStride(image, 0, &yStride);
            AImage_getPlaneRowStride(image, 1, &uvStride);


            AImage_getPlaneData(image, 0, &yPixel, &yLen);
            AImage_getPlaneData(image, 1, &vPixel, &vLen);
            AImage_getPlaneData(image, 2, &uPixel, &uLen);
            AImageCropRect srcRect;
            AImage_getCropRect(image, &srcRect);
            int flatSize = height * width;

            auto dataY = new uint8_t[flatSize];
            auto dataU = new uint8_t[flatSize >> 2];
            auto dataV = new uint8_t[flatSize >> 2];

            data_y_ = dataY;
            data_u_ = dataU;
            data_v_ = dataV;
            int32_t yPixelStride;
            int32_t uvPixelStride;
            AImage_getPlanePixelStride(image, 0, &yPixelStride);
            AImage_getPlanePixelStride(image, 1, &uvPixelStride);

            for (int32_t y = 0; y < height; y++) {
                uint8_t *pY = yPixel + yStride * (y + srcRect.top) + srcRect.left; // 裁剪区域开始位置

                // uv 为四像素公用一个，Y[x, y] 的位置对应 UV[x/2, y/2] 的位置。
                int32_t uv_row_start = uvStride * ((y + srcRect.top) >> 1); // 行模 2
                const uint8_t *pU = uPixel + uv_row_start + (srcRect.left >> 1); // 列模 2
                const uint8_t *pV = vPixel + uv_row_start + (srcRect.left >> 1);

                for (int32_t x = 0; x < width; x++) {
                    dataY[x] = pY[x];
                    const int32_t uv_offset = (x >> 1) * uvPixelStride;
                    dataU[x >> 1] = pU[uv_offset];
                    dataV[x >> 1] = pV[uv_offset];
                }
                dataY += width;
                if (y & 1) { // 两行复用一个 u/v
                    dataU += width >> 1;
                    dataV += width >> 1;
                }
            }
        }


        AndroidVideoI420Buffer::~AndroidVideoI420Buffer() {
            delete data_y_;
            delete data_u_;
            delete data_v_;
        }
    }  // namespace


    rtc::scoped_refptr<AndroidVideoFrameBuffer> AndroidVideoFrameBuffer::Create(AImage *image) {
        return rtc::make_ref_counted<AndroidVideoFrameBuffer>(image);
    }


    AndroidVideoFrameBuffer::AndroidVideoFrameBuffer(AImage *image) : image_(image) {
        AImageCropRect srcRect;
        AImage_getCropRect(image, &srcRect);
        AImage_getWidth(image, &width_);
        AImage_getHeight(image, &height_);
        width_ = MIN(width_, (srcRect.right - srcRect.left));
        height_ = MIN(height_, (srcRect.bottom - srcRect.top));
    }


    AndroidVideoFrameBuffer::~AndroidVideoFrameBuffer() {
        if (image_) {
            AImage_delete(image_);
        }
    }


    rtc::scoped_refptr<webrtc::VideoFrameBuffer> AndroidVideoFrameBuffer::CropAndScale(
            int crop_x,
            int crop_y,
            int crop_width,
            int crop_height,
            int scale_width,
            int scale_height) {
        return nullptr;
    }


    AndroidVideoFrameBuffer::Type AndroidVideoFrameBuffer::type() const {
//        return Type::kNative;
        return Type::kI420;
    }


    int AndroidVideoFrameBuffer::width() const {
        return width_;
    }


    int AndroidVideoFrameBuffer::height() const {
        return height_;
    }


    rtc::scoped_refptr<webrtc::I420BufferInterface> AndroidVideoFrameBuffer::ToI420() {
        // We don't need to retain the buffer because toI420 returns a new object that
        // we are assumed to take the ownership of.
        return rtc::make_ref_counted<AndroidVideoI420Buffer>(width_, height_, image_);
    }

    const I420BufferInterface * AndroidVideoFrameBuffer::GetI420() const {
        return new rtc::RefCountedObject<AndroidVideoI420Buffer>(width_, height_, image_);
    }

}  // namespace webrtc
