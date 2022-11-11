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

#ifndef CAMERA_NATIVE_CAMERA_H
#define CAMERA_NATIVE_CAMERA_H

#include <string>
#include <vector>
#include <map>
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraDevice.h>
#include <camera/NdkCameraMetadataTags.h>
#include "image_reader.h"
#include "camera.h"

class CameraManager {
public:
    CameraManager();


    ~CameraManager();


    /**
     * EnumerateCamera()
     *     Loop through cameras on the system, pick up
     *     1) back facing one if available
     *     2) otherwise pick the first one reported to us
     */
    std::unique_ptr<Camera> GetCamera(acamera_metadata_enum_acamera_lens_facing camera_facing);


    void OnCameraStatusChanged(const char *id, bool available);


    void OnDeviceState(ACameraDevice *dev);


    void OnDeviceError(ACameraDevice *dev, int err);


private:
    ACameraManager_AvailabilityCallbacks *GetManagerListener();


    void EnumerateCamera();


    std::shared_ptr<ACameraManager> camera_mgr_;
    std::map<std::string, std::unique_ptr<CameraInfo>> cameras_;
};

#endif  // CAMERA_NATIVE_CAMERA_H
