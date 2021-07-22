/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "camera/camera_engine.h"
#include "utils/native_debug.h"
#include "testthread/test_thread.h"
/*
 * SampleEngine global object
 */
static CameraEngine* pEngineObj = nullptr;
CameraEngine* GetAppEngine() {
  ASSERT(pEngineObj, "AppEngine has not initialized");
  return pEngineObj;
}

/**
 * Teamplate function for NativeActivity derived applications
 *   Create/Delete camera object with
 *   INIT_WINDOW/TERM_WINDOW command, ignoring other event.
 */
static void ProcessAndroidCmd(struct android_app* app, int32_t cmd) {
  auto* engine = reinterpret_cast<CameraEngine*>(app->userData);
  switch (cmd) {
    case APP_CMD_INIT_WINDOW:
      if (engine->AndroidApp()->window != nullptr) {
        engine->SaveNativeWinRes(ANativeWindow_getWidth(app->window),
                                 ANativeWindow_getHeight(app->window),
                                 ANativeWindow_getFormat(app->window));
        engine->OnAppInitWindow();
      }
      break;
    case APP_CMD_TERM_WINDOW:
      engine->OnAppTermWindow();
      ANativeWindow_setBuffersGeometry(
          app->window, engine->GetSavedNativeWinWidth(),
          engine->GetSavedNativeWinHeight(), engine->GetSavedNativeWinFormat());
      break;
    case APP_CMD_CONFIG_CHANGED:
      engine->OnAppConfigChange();
      break;
    case APP_CMD_LOST_FOCUS:
    default:
      break;
  }
}

extern "C" void android_main(struct android_app* state) {
  test_thread();
//  CameraEngine engine(state);
  pEngineObj = new CameraEngine(state);

  state->userData = reinterpret_cast<void*>(pEngineObj);
  state->onAppCmd = ProcessAndroidCmd;

  // loop waiting for stuff to do.
  while (true) {
    // Read all pending events.
    int events;
    struct android_poll_source* source;

    while (ALooper_pollAll(0, nullptr, &events, (void**)&source) >= 0) {
      // Process this event.
      if (source != nullptr) {
        source->process(state, source);
      }

      // Check if we are exiting.
      if (state->destroyRequested != 0) {
        LOGI("CameraEngine thread destroy requested!");
        pEngineObj->DeleteCamera();
        pEngineObj = nullptr;
        return;
      }
    }
    pEngineObj->DrawFrame();
  }
}

/**
 * Handle Android System APP_CMD_INIT_WINDOW message
 *   Request camera persmission from Java side
 *   Create camera object if camera has been granted
 */
void CameraEngine::OnAppInitWindow() {
  if (!cameraGranted_) {
    // Not permitted to use camera yet, ask(again) and defer other events
    RequestCameraPermission();
    return;
  }

  rotation_ = GetDisplayRotation();

  CreateCamera();
  ASSERT(camera_, "CameraCreation Failed");

  EnableUI();

  // NativeActivity end is ready to display, start pulling images
  cameraReady_ = true;
  camera_->StartPreview(true);
}

/**
 * Handle APP_CMD_TEMR_WINDOW
 */
void CameraEngine::OnAppTermWindow() {
  cameraReady_ = false;
  DeleteCamera();
}

/**
 * Handle APP_CMD_CONFIG_CHANGED
 */
void CameraEngine::OnAppConfigChange() {
  int newRotation = GetDisplayRotation();

  if (newRotation != rotation_) {
    OnAppTermWindow();

    rotation_ = newRotation;
    OnAppInitWindow();
  }
}

/**
 * Retrieve saved native window width.
 * @return width of native window
 */
int32_t CameraEngine::GetSavedNativeWinWidth() const {
  return savedNativeWinRes_.width;
}

/**
 * Retrieve saved native window height.
 * @return height of native window
 */
int32_t CameraEngine::GetSavedNativeWinHeight() const {
  return savedNativeWinRes_.height;
}

/**
 * Retrieve saved native window format
 * @return format of native window
 */
int32_t CameraEngine::GetSavedNativeWinFormat() const {
  return savedNativeWinRes_.format;
}

/**
 * Save original NativeWindow Resolution
 * @param w width of native window in pixel
 * @param h height of native window in pixel
 * @param format
 */
void CameraEngine::SaveNativeWinRes(int32_t w, int32_t h, int32_t format) {
  savedNativeWinRes_.width = w;
  savedNativeWinRes_.height = h;
  savedNativeWinRes_.format = format;
}
