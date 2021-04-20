//
// Created by Albert on 2020/9/14.
//

#ifndef ANDROIDTEST_COLOR_H
#define ANDROIDTEST_COLOR_H

extern "C" JNIEXPORT void JNICALL
surfaceCreated(
        JNIEnv *env, jobject thiz, jint color);

extern "C" JNIEXPORT void JNICALL
surfaceChanged(
        JNIEnv *env, jobject thiz, jint width, jint height);


extern "C" JNIEXPORT void JNICALL
onDrawFrame(
        JNIEnv *env, jobject thiz);

#endif //ANDROIDTEST_COLOR_H
