/*
 *  Copyright (c) 2016 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#include "android_video_track_source.h"
#include "android_video_frame_buffer.h"

#include <utility>

#include "rtc_base/logging.h"
#include "android_video_sink.h"

namespace rtc_demo {

    // MediaCodec wants resolution to be divisible by 2.
    const int kRequiredResolutionAlignment = 2;

    AndroidVideoTrackSource::AndroidVideoTrackSource(
            rtc::Thread *signaling_thread,
            bool is_screencast,
            bool align_timestamps
    ) : AdaptedVideoTrackSource(kRequiredResolutionAlignment),
        signaling_thread_(signaling_thread),
        is_screencast_(is_screencast),
        align_timestamps_(align_timestamps){

    }

    AndroidVideoTrackSource::~AndroidVideoTrackSource() {
    }

    AndroidVideoTrackSource::SourceState AndroidVideoTrackSource::state() const {
        return state_.load();
    }

    void AndroidVideoTrackSource::SetState(bool is_live) {
        const SourceState state = is_live ? kLive : kEnded;
        if (state_.exchange(state) != state) {
            if (rtc::Thread::Current() == signaling_thread_) {
                FireOnChanged();
            } else {
                // TODO(sakal): Is this even necessary, does FireOnChanged have to be
                // called from signaling thread?
                signaling_thread_->PostTask(RTC_FROM_HERE, [this] { FireOnChanged(); });
            }
        }
    }


    bool AndroidVideoTrackSource::remote() const  {
        return false;
    }


    bool AndroidVideoTrackSource::is_screencast() const {
        return is_screencast_.load();
    }


    absl::optional<bool> AndroidVideoTrackSource::needs_denoising() const {
        return true;
    }


    void AndroidVideoTrackSource::OnFrameCaptured(AImage *image, int32_t rotation) {
        int format = 0;
        AImage_getFormat(image, &format);
        if (format == AIMAGE_FORMAT_YUV_420_888) {
            rtc::scoped_refptr<VideoFrameBuffer> buffer = AndroidVideoFrameBuffer::Create(image);

            int64_t timestamp_ns = 0;
            AImage_getTimestamp(image, &timestamp_ns);
            AImage_getTimestamp(image, &timestamp_ns);
            // notify sink video is update.
            OnFrame(VideoFrame::Builder()
                            .set_video_frame_buffer(buffer)
                            .set_rotation(ConvertRotation(rotation))
                            .set_timestamp_us(timestamp_ns / rtc::kNumNanosecsPerMicrosec)
                            .build());
        }
    }

    VideoRotation AndroidVideoTrackSource::ConvertRotation(int32_t rotation) {
        if (rotation <= 45 || rotation > 315) {
            return VideoRotation::kVideoRotation_90;
        } else if (rotation <= 135) {
            return VideoRotation::kVideoRotation_180;
        } else if (rotation <= 225) {
            return VideoRotation::kVideoRotation_270;
        } else {
            return VideoRotation::kVideoRotation_0;
        }
    }
}  // namespace webrtc
