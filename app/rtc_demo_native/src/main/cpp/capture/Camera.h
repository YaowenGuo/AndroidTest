//
// Created by Albert on 2021/5/15.
//

#ifndef ANDROIDTEST_CAMERA_H
#define ANDROIDTEST_CAMERA_H
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraMetadataTags.h>
#include <camera/NdkCameraMetadata.h>
#include "../base/esUtil.h"
#include <media/NdkImageReader.h>
#include <media/NdkMediaFormat.h>
#include <thread>

#include <android/native_window.h>
#include <android_native_app_glue.h>
#include <functional>

#define MAX_BUF_COUNT 4

class Camera {
public:
    explicit Camera(android_app* app, acamera_metadata_enum_acamera_lens_facing findFacing);
    ~Camera();

    static void onDisconnected(void *context, ACameraDevice *device);

    static void onError(void *context, ACameraDevice *device, int error);

    static void imageCallback(void *context, AImageReader *reader);

    AImageReader *createJpegReader();

    AImageReader* createYuvReader();

    AImage * nextImage();
    void deleteImage(AImage *image);
    int displayImage(AImage *image, ANativeWindow_Buffer *buf);
    void PresentImage(ANativeWindow_Buffer* buf, AImage* image);
    void PresentImage90(ANativeWindow_Buffer* buf, AImage* image);
    void PresentImage180(ANativeWindow_Buffer* buf, AImage* image);
    void PresentImage270(ANativeWindow_Buffer* buf, AImage* image);
    void CreateSession(ANativeWindow* previewWindow, int32_t imageRotation);
private:
    ACameraDevice_stateCallbacks* initCallback();

    ACameraManager *_cameraManager = nullptr;
    ACameraDevice *_cameraDevice = nullptr;
    android_app *_app;
    AImageReader *_imageReader = nullptr;
    const char * _cameraId = nullptr;
    ACameraCaptureSession* captureSession_;
    ACaptureSessionOutputContainer* outputContainer_;
    int32_t sensitivity_;
    int64_t exposureTime_;

};


#endif //ANDROIDTEST_CAMERA_H
