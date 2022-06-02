/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#include <jni.h>

#include "rtc_base/ssl_adapter.h"
#include <sdk/android/native_api/base/init.h>

#include "utils/jvm.h"
#include "utils/class_loader.h"

namespace jni {

extern "C" jint JNIEXPORT JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved) {
  jint ret = InitGlobalJniVariables(jvm);
  webrtc::InitAndroid(::jni::GetJVM());
  RTC_DCHECK_GE(ret, 0);
  if (ret < 0)
    return -1;

  RTC_CHECK(rtc::InitializeSSL()) << "Failed to InitializeSSL()";
  rtc_demo::InitClassLoader(GetEnv());
  return ret;
}

extern "C" void JNIEXPORT JNICALL JNI_OnUnLoad(JavaVM* jvm, void* reserved) {
  RTC_CHECK(rtc::CleanupSSL()) << "Failed to CleanupSSL()";
  ClearGlobalJniVariables();
}

}  // namespace jni
