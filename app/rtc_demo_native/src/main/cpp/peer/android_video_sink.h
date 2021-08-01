//
// Created by Albert on 2021/7/21.
//

#ifndef ANDROIDTEST_ANDROID_VIDEO_SINK_H
#define ANDROIDTEST_ANDROID_VIDEO_SINK_H

#include <api/video/video_sink_interface.h>
#include <api/video/video_frame.h>
#include <android_native_app_glue.h>
#include <android/native_window.h>
#include "utils/native_debug.h"

using webrtc::VideoFrame;

namespace rtc_demo {
    class AndroidVideoSink : public rtc::VideoSinkInterface<VideoFrame> {
    public:
        AndroidVideoSink(ANativeWindow *window);


        ~AndroidVideoSink() override;


    private:
        void OnFrame(const VideoFrame &frame) override;


        void PresentImage(ANativeWindow_Buffer *,
                          const rtc::scoped_refptr<webrtc::I420BufferInterface> &);


        void
        PresentImage90(ANativeWindow_Buffer *, rtc::scoped_refptr<webrtc::I420BufferInterface>);


        void
        PresentImage180(ANativeWindow_Buffer *, rtc::scoped_refptr<webrtc::I420BufferInterface>);


        void
        PresentImage270(ANativeWindow_Buffer *, rtc::scoped_refptr<webrtc::I420BufferInterface>);


        ANativeWindow *window_;
    };
}  // namespace rtc_demo


#endif //ANDROIDTEST_ANDROID_VIDEO_SINK_H
