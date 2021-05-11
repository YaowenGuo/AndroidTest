//
// Created by Albert on 4/25/21.
//

#include "Capture.h"
#include <camera/NdkCameraManager.h>


void createCamera() {
    ACameraManager *camManager = ACameraManager_create();
    ACameraIdList *cameraIds = nullptr;
    ACameraManager_getCameraIdList(camManager, &cameraIds);

    for (int i = 0; i < cameraIds->numCameras; ++i)
    {
        const char* id = cameraIds->cameraIds[i];
        ACameraMetadata* metadataObj;
        ACameraManager_getCameraCharacteristics(camManager, id, &metadataObj);

        // Work with metadata here
        // ...

        ACameraMetadata_free(metadataObj);

    }
    ACameraManager_deleteCameraIdList(cameraIds);
    ACameraManager_delete(camManager);
}

void clearCamera() {
}