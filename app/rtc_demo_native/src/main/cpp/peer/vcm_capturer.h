/*
 *  Copyright (c) 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
#ifndef TEST_VCM_CAPTURER_H_
#define TEST_VCM_CAPTURER_H_

#include <memory>
#include <vector>

#include "api/scoped_refptr.h"
#include "modules/video_capture/video_capture.h"
#include "test/test_video_capturer.h"

using namespace webrtc;

namespace rtc_demo {

    class VcmCapturer : public rtc::VideoSourceInterface<VideoFrame>,
                        public rtc::VideoSinkInterface<VideoFrame> {
    public:
        class FramePreprocessor {
        public:
            virtual ~FramePreprocessor() = default;

            virtual VideoFrame Preprocess(const VideoFrame &frame) = 0;
        };

        static VcmCapturer *Create(size_t width,
                                   size_t height,
                                   size_t target_fps,
                                   size_t capture_device_index);

        virtual ~VcmCapturer();

        void OnFrame(const VideoFrame &frame) override;

        void AddOrUpdateSink(rtc::VideoSinkInterface<VideoFrame> *sink,
                             const rtc::VideoSinkWants &wants) override;

        void RemoveSink(rtc::VideoSinkInterface<VideoFrame> *sink) override;

//        void SetFramePreprocessor(std::unique_ptr<FramePreprocessor> preprocessor) {
//            MutexLock lock(&lock_);
//            preprocessor_ = std::move(preprocessor);
//        }

    private:
        VcmCapturer();

        bool Init(size_t width,
                  size_t height,
                  size_t target_fps,
                  size_t capture_device_index);
        void UpdateVideoAdapter();
        void Destroy();
        Mutex lock_;
        rtc::scoped_refptr<VideoCaptureModule> vcm_;
        VideoCaptureCapability capability_;
        std::unique_ptr<FramePreprocessor> preprocessor_ RTC_GUARDED_BY(lock_);
        rtc::VideoBroadcaster broadcaster_;
        cricket::VideoAdapter video_adapter_;
    };

}  // namespace rtc_demo

#endif  // TEST_VCM_CAPTURER_H_
