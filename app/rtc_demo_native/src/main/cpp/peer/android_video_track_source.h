/*
 *  Copyright (c) 2016 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#ifndef API_ANDROID_JNI_ANDROIDVIDEOTRACKSOURCE_H_
#define API_ANDROID_JNI_ANDROIDVIDEOTRACKSOURCE_H_

#include <jni.h>
#include <media/NdkImage.h>

#include "media/base/adapted_video_track_source.h"
#include "rtc_base/thread.h"
#include "rtc_base/timestamp_aligner.h"

#include "api/scoped_refptr.h"
#include "modules/video_capture/video_capture.h"
#include "test/test_video_capturer.h"

namespace rtc_demo {

    class AndroidVideoTrackSource : public rtc::AdaptedVideoTrackSource {
    public:
        AndroidVideoTrackSource(rtc::Thread *signaling_thread,
                                bool is_screencast,
                                bool align_timestamps);

        ~AndroidVideoTrackSource() override;

        // 「------------------
        SourceState state() const override;

        // 是否是远端接收
        bool remote() const override;

        // 是否是录屏
        bool is_screencast() const override;

        // 是否需要降噪
        absl::optional<bool> needs_denoising() const override;
        // L------------------

        // Add image when captured.
        void OnFrameCaptured(AImage *image, int32_t);
        void SetState(bool is_live);

    private:
        rtc::Thread *signaling_thread_;
        std::atomic<SourceState> state_;
        std::atomic<bool> is_screencast_;
        rtc::TimestampAligner timestamp_aligner_;
        const bool align_timestamps_;
    };


} // namespace rtc_demo

#endif  // API_ANDROID_JNI_ANDROIDVIDEOTRACKSOURCE_H_
