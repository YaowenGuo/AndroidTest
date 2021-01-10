#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_tech_yaowen_test_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from FFmpeg C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" {
#include "libavutil/avutil.h"
}

/**
 * 拿到 ffmpeg 当前版本
 * @return
 */
const char *getFFmpegVer() {
    return av_version_info();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_tech_yaowen_ffmpeg_libavutil_AVUtil_getFFmpegVersion(JNIEnv *env, jclass jClass) {
    return env->NewStringUTF(av_version_info());
}