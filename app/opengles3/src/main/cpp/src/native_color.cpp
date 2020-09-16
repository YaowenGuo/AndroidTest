//
// Created by Albert on 2020/9/14.
//

#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "../include/native_color.h"


/**
 * 动态注册
 */
/*JNINativeMethod methods[] = {
        {"surfaceCreated", "(I)V",  (void *) surfaceCreated},
        {"surfaceChanged", "(II)V", (void *) surfaceChanged},
        {"onDrawFrame",    "()V",   (void *) onDrawFrame}
};*/

/**
 * 动态注册
 * @param env
 * @return
 */
/*
jint registerNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("tech/yaowen/opengles3/renderer/NativeColorRenderer");
    if ((env->RegisterNatives(cl, methods, sizeof(methods) / sizeof(methods[0]))) < 0) {
        return -1;
    }
    return 0;
}
*/

/**
 * 加载默认回调
 * @param vm
 * @param reserved
 * @return
 */
/*jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    //注册方法
    if (registerNativeMethod(env) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}*/


extern "C"
JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_surfaceCreated(
        JNIEnv *env,
        jobject thiz,
        jint color) {
    //分离RGBA的百分比
    GLfloat redF = ((color >> 16) & 0xFF) * 1.0f / 255;
    GLfloat greenF = ((color >> 8) & 0xFF) * 1.0f / 255;
    GLfloat blueF = (color & 0xFF) * 1.0f / 255;
    GLfloat alphaF = ((color >> 24) & 0xFF) * 1.0f / 255;
    glClearColor(redF, greenF, blueF, alphaF);

}

extern "C"
JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_surfaceChanged(JNIEnv *env, jobject thiz,
                                                                       jint width, jint height) {
    glViewport(0, 0, width, height);
}


extern "C"
JNIEXPORT void JNICALL
Java_tech_yaowen_opengles3_renderer_NativeColorRenderer_onDrawFrame(JNIEnv *env, jobject thiz) {
    glClear(GL_COLOR_BUFFER_BIT);
}