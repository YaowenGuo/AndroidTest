/*
 *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#include <algorithm>
#include <string>

#include "rtc_base/checks.h"
#include "jni_generator_helper.h"
#include "class_loader.h"
#include "java_types.h"

// Abort the process if `jni` has a Java exception pending. This macros uses the
// comma operator to execute ExceptionDescribe and ExceptionClear ignoring their
// return values and sending "" to the error stream.
#define CHECK_EXCEPTION(jni)        \
  RTC_CHECK(!jni->ExceptionCheck()) \
      << (jni->ExceptionDescribe(), jni->ExceptionClear(), "")
using jni_generator::JniJavaCallContextChecked;

namespace rtc_demo {

    // Step 1: Forward declarations.
    extern const char kClassPath_org_webrtc_WebRtcClassLoader[];
    const char kClassPath_org_webrtc_WebRtcClassLoader[] = "org/webrtc/WebRtcClassLoader";
    // Leaking this jclass as we cannot use LazyInstance from some threads.
    std::atomic<jclass>g_org_webrtc_WebRtcClassLoader_clazz(nullptr);
    #ifndef rtc_demo_WebRtcClassLoader_clazz_defined
    #define rtc_demo_WebRtcClassLoader_clazz_defined
    inline jclass org_webrtc_WebRtcClassLoader_clazz(JNIEnv *env) {
        return LazyGetClass(env, kClassPath_org_webrtc_WebRtcClassLoader,
                                           &g_org_webrtc_WebRtcClassLoader_clazz);
    }
    #endif

    static std::atomic<jmethodID> g_org_webrtc_WebRtcClassLoader_getClassLoader(nullptr);


    static ScopedJavaLocalRef <jobject>
    rtcdemo_ClassLoader_getClassLoader(JNIEnv *env) {
        jclass clazz = org_webrtc_WebRtcClassLoader_clazz(env);
        CHECK_CLAZZ(env, clazz,
                    org_webrtc_WebRtcClassLoader_clazz(env), NULL);

        JniJavaCallContextChecked call_context;
        call_context.Init<
                base::android::MethodID::TYPE_STATIC>(
                env,
                clazz,
                "getClassLoader",
                "()Ljava/lang/Object;",
                &g_org_webrtc_WebRtcClassLoader_getClassLoader);

        jobject ret =
                env->CallStaticObjectMethod(clazz,
                                            call_context.base.method_id);
        return ScopedJavaLocalRef<jobject>(env, ret);
    }

    namespace {

        class ClassLoader {
        public:
            explicit ClassLoader(JNIEnv *env)
                    : class_loader_(rtcdemo_ClassLoader_getClassLoader(env)) {
                class_loader_class_ = reinterpret_cast<jclass>(
                        env->NewGlobalRef(env->FindClass("java/lang/ClassLoader")));
                CHECK_EXCEPTION(env);
                load_class_method_ =
                        env->GetMethodID(class_loader_class_, "loadClass",
                                         "(Ljava/lang/String;)Ljava/lang/Class;");
                CHECK_EXCEPTION(env);
            }


            ScopedJavaLocalRef <jclass> FindClass(JNIEnv *env, const char *c_name) {
                // ClassLoader.loadClass expects a classname with components separated by
                // dots instead of the slashes that JNIEnv::FindClass expects.
                std::string name(c_name);
                std::replace(name.begin(), name.end(), '/', '.');
                ScopedJavaLocalRef <jstring> j_name = NativeToJavaString(env, name);
                const jclass clazz = static_cast<jclass>(env->CallObjectMethod(
                        class_loader_.obj(), load_class_method_, j_name.obj()));
                CHECK_EXCEPTION(env);
                return ScopedJavaLocalRef<jclass>(env, clazz);
            }


        private:
            ScopedJavaGlobalRef <jobject> class_loader_;
            jclass class_loader_class_;
            jmethodID load_class_method_;
        };

        static ClassLoader *g_class_loader = nullptr;

    }  // namespace

    void InitClassLoader(JNIEnv *env) {
        RTC_CHECK(g_class_loader == nullptr);
        g_class_loader = new ClassLoader(env);
    }


    ScopedJavaLocalRef <jclass> GetClass(JNIEnv *env, const char *name) {
        // The class loader will be null in the JNI code called from the ClassLoader
        // ctor when we are bootstrapping ourself.
        return (g_class_loader == nullptr)
               ? ScopedJavaLocalRef<jclass>(env, env->FindClass(name))
               : g_class_loader->FindClass(env, name);
    }

}  // namespace webrtc
