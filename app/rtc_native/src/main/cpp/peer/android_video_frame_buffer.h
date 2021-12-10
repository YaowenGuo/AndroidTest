/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#ifndef SDK_ANDROID_VIDEO_FRAME_H_
#define SDK_ANDROID_VIDEO_FRAME_H_

#include <jni.h>

#include <media/NdkImageReader.h>
#include <api/video/video_frame_buffer.h>
#include <api/video/video_rotation.h>
#include <utils/jni_helpers.h>

namespace rtc_demo {

    class AndroidVideoFrameBuffer : public webrtc::VideoFrameBuffer {
    public:
        // Creates a native VideoFrameBuffer from a Java VideoFrame.Buffer.
        static rtc::scoped_refptr<AndroidVideoFrameBuffer> Create(AImage *);


        ~AndroidVideoFrameBuffer() override;


        // Crops a region defined by |crop_x|, |crop_y|, |crop_width| and
        // |crop_height|. Scales it to size |scale_width| x |scale_height|.
        rtc::scoped_refptr<VideoFrameBuffer> CropAndScale(
                int crop_x,
                int crop_y,
                int crop_width,
                int crop_height,
                int scale_width,
                int scale_height) override;


    protected:
        // Should not be called directly. Adopts the Java VideoFrame.Buffer. Use
        // Create() or Adopt() instead for clarity.
        AndroidVideoFrameBuffer(AImage *);


    private:
        Type type() const override;


        int width() const override;


        int height() const override;


        rtc::scoped_refptr<webrtc::I420BufferInterface> ToI420() override;


        int width_ = 0;
        int height_ = 0;
        // Holds a VideoFrame.Buffer.
        AImage *image_;
    };

}  // namespace webrtc

#endif  // SDK_ANDROID_SRC_JNI_VIDEO_FRAME_H_
