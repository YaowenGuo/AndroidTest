#include <utility>
#include <queue>
#include <unistd.h>
#include <cinttypes>
#include <camera/NdkCameraManager.h>
#include "camera_manager.h"
#include "utils/native_debug.h"
#include "utils/camera_utils.h"

auto aCameraManagerDel = [](ACameraManager* pcm) {
    ACameraManager_delete(pcm);
};

CameraManager::CameraManager()
        : camera_mgr_(ACameraManager_create(), aCameraManagerDel) {
    ASSERT(camera_mgr_, "Failed to create cameraManager");
    EnumerateCamera();
    CALL_MGR(registerAvailabilityCallback(camera_mgr_.get(), GetManagerListener()));
}


CameraManager::~CameraManager() {
    for (auto &cam: cameras_) {
        if (cam.second->device_) {
            CALL_DEV(close(cam.second->device_));
        }
    }
    cameras_.clear();
    CALL_MGR(unregisterAvailabilityCallback(camera_mgr_.get(), GetManagerListener()));
}



/*
 * Camera Manager Listener object
 */
void OnCameraAvailable(void *ctx, const char *id) {
    reinterpret_cast<CameraManager *>(ctx)->OnCameraStatusChanged(id, true);
}


void OnCameraUnavailable(void *ctx, const char *id) {
    reinterpret_cast<CameraManager *>(ctx)->OnCameraStatusChanged(id, false);
}


/**
 * Construct a camera manager listener on the fly and return to caller
 *
 * @return ACameraManager_AvailabilityCallback
 */
ACameraManager_AvailabilityCallbacks *CameraManager::GetManagerListener() {
    static ACameraManager_AvailabilityCallbacks cameraMgrListener = {
            .context = this,
            .onCameraAvailable = ::OnCameraAvailable,
            .onCameraUnavailable = ::OnCameraUnavailable,
    };
    return &cameraMgrListener;
}

void CameraManager::EnumerateCamera() {
    ACameraIdList *cameraIds = nullptr;
    CALL_MGR(getCameraIdList(camera_mgr_.get(), &cameraIds));

    for (int i = 0; i < cameraIds->numCameras; ++i) {
        const char *id = cameraIds->cameraIds[i];

        ACameraMetadata *metadataObj;
        CALL_MGR(getCameraCharacteristics(camera_mgr_.get(), id, &metadataObj));

        int32_t count = 0;
        const uint32_t *tags = nullptr;
        ACameraMetadata_getAllTags(metadataObj, &count, &tags);
        for (int tagIdx = 0; tagIdx < count; ++tagIdx) {
            if (ACAMERA_LENS_FACING == tags[tagIdx]) {
                ACameraMetadata_const_entry lensInfo = {
                        0,
                };
                CALL_METADATA(getConstEntry(metadataObj, tags[tagIdx], &lensInfo));
                auto cam = std::make_unique<CameraInfo>(id);
                cam->facing_ = static_cast<acamera_metadata_enum_android_lens_facing_t>(
                        lensInfo.data.u8[0]);
                cam->owner_ = false;
                cam->device_ = nullptr;
                cameras_[cam->id_] = std::move(cam);
                break;
            }
        }
        ACameraMetadata_free(metadataObj);
    }

    ASSERT(!cameras_.empty(), "No Camera Available on the device");
    ACameraManager_deleteCameraIdList(cameraIds);
}


std::unique_ptr<Camera> CameraManager::GetCamera(acamera_metadata_enum_acamera_lens_facing
    camera_facing) {
    CameraInfo* info_p = nullptr;
    for(auto const& [key, camera] : cameras_){
        if(camera->facing_ == camera_facing/* && camera->available_*/) {
            info_p = camera.get();
            break;
        }
    }
    return info_p ? std::make_unique<Camera>(camera_mgr_, *info_p) : std::unique_ptr<Camera>(nullptr);
}


