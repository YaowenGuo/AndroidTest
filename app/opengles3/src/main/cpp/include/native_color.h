//
// Created by Albert on 2020/9/14.
//

#ifndef ANDROIDTEST_NATIVE_COLOR_H
#define ANDROIDTEST_NATIVE_COLOR_H

extern "C" JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_surfaceCreated(
        JNIEnv *env, jobject thiz, jint color);

extern "C" JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_surfaceChanged(
        JNIEnv *env, jobject thiz, jint width, jint height);


extern "C" JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_onDrawFrame(
        JNIEnv *env, jobject thiz);

#endif //ANDROIDTEST_NATIVE_COLOR_H
