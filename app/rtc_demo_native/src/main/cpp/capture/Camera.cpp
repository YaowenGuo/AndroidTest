//
// Created by Albert on 2021/5/15.
//

#include "Camera.h"

static inline uint32_t YUV2RGB(int nY, int nU, int nV) {
    nY -= 16;
    nU -= 128;
    nV -= 128;
    if (nY < 0) nY = 0;

    // This is the floating point equivalent. We do the conversion in integer
    // because some Android devices do not have floating point in hardware.
    // nR = (int)(1.164 * nY + 1.596 * nV);
    // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
    // nB = (int)(1.164 * nY + 2.018 * nU);

    int nR = (int)(1192 * nY + 1634 * nV);
    int nG = (int)(1192 * nY - 833 * nV - 400 * nU);
    int nB = (int)(1192 * nY + 2066 * nU);

    nR = MIN(kMaxChannelValue, MAX(0, nR));
    nG = MIN(kMaxChannelValue, MAX(0, nG));
    nB = MIN(kMaxChannelValue, MAX(0, nB));

    nR = (nR >> 10) & 0xff;
    nG = (nG >> 10) & 0xff;
    nB = (nB >> 10) & 0xff;

    return 0xff000000 | (nR << 16) | (nG << 8) | nB;
}


Camera::Camera(android_app *app, acamera_metadata_enum_acamera_lens_facing findFacing) {
    _cameraManager = ACameraManager_create();

    ACameraIdList *cameraIds = nullptr;
    ACameraManager_getCameraIdList(_cameraManager, &cameraIds);

    const char *id = nullptr;
    ACameraMetadata *metadataObj = nullptr;
    for (int i = 0; i < cameraIds->numCameras; ++i) {
        id = cameraIds->cameraIds[i];
        ACameraManager_getCameraCharacteristics(_cameraManager, id, &metadataObj);

        int32_t count = 0;
        const uint32_t *tags = nullptr;
        ACameraMetadata_getAllTags(metadataObj, &count, &tags);

        for (int tagIdx = 0; tagIdx < count; ++tagIdx) {
            // We are interested in entry that describes the facing of camera
            if (ACAMERA_LENS_FACING == tags[tagIdx]) {
                ACameraMetadata_const_entry lensInfo = {0};
                camera_status_t status = ACameraMetadata_getConstEntry(metadataObj, tags[tagIdx],
                                                                       &lensInfo);
                if (status == ACAMERA_OK) {
                    // Great, entry available
                }
                auto facing = static_cast<acamera_metadata_enum_android_lens_facing_t>(lensInfo.data.u8[0]);

                if (facing == findFacing) {
                    break;
                }
            }
        }

        // 曝光时间范围
        ACameraMetadata_const_entry entry = {0};
        ACameraMetadata_getConstEntry(metadataObj, ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE, &entry);

        int64_t minExposure = entry.data.i64[0];
        int64_t maxExposure = entry.data.i64[1];

        ACameraMetadata_getConstEntry(metadataObj, ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE, &entry);


        // ISO range 感光度范围
        ACameraMetadata_getConstEntry(metadataObj, ACAMERA_SENSOR_INFO_SENSITIVITY_RANGE, &entry);

        int32_t minSensitivity = entry.data.i32[0];
        int32_t maxSensitivity = entry.data.i32[1];


        ACameraMetadata_getConstEntry(metadataObj, ACAMERA_SCALER_AVAILABLE_STREAM_CONFIGURATIONS,
                                      &entry);

        for (int streamIndex = 0; streamIndex < entry.count; streamIndex += 4) {
            // We are only interested in output streams, so skip input stream
            int32_t input = entry.data.i32[streamIndex + 3];
            if (input)
                continue;

            int32_t format = entry.data.i32[streamIndex + 0];
            if (format == AIMAGE_FORMAT_JPEG) {
                int32_t width = entry.data.i32[streamIndex + 1];
                int32_t height = entry.data.i32[streamIndex + 2];
            }
        }

        ACameraMetadata_getConstEntry(metadataObj, ACAMERA_SENSOR_ORIENTATION, &entry);
        int32_t orientation = entry.data.i32[0];

        ACameraMetadata_free(metadataObj);

    }
    _cameraDevice = nullptr;
    if (id != nullptr) {
        ACameraManager_openCamera(_cameraManager, id, initCallback(), &_cameraDevice);
    }

    if (_cameraDevice != nullptr) {
        _imageReader = createYuvReader();
//        _imageReader = createJpegReader();
    }

    CreateSession(app->window, 0);


    ACameraManager_deleteCameraIdList(cameraIds);
    _cameraId = id;
}

Camera::~Camera() {
    ACameraManager_delete(_cameraManager);
}

void Camera::onDisconnected(void *context, ACameraDevice *device) {
    // ...
}

void Camera::onError(void *context, ACameraDevice *device, int error) {
    LOGE("RTC DEMO: open camera error. code = %d", error);
}

ACameraDevice_stateCallbacks *Camera::initCallback() {
    return new ACameraDevice_stateCallbacks{
            .context = this,
            .onDisconnected = onDisconnected,
            .onError = onError,
    };
}

void Camera::imageCallback(void *context, AImageReader *reader) {
    AImage *image = nullptr;
    auto status = AImageReader_acquireNextImage(reader, &image);
    // Check status here ...

    // Try to process data without blocking the callback
//    std::thread processor([=]() {
//        uint8_t *data = nullptr;
//        int len = 0;
//        AImage_getPlaneData(image, 0, &data, &len);

    // Process data here
    // ...

//        AImage_delete(image);

//    });
//    processor.detach();
}

AImage *Camera::nextImage() {
    if (_imageReader == nullptr) return nullptr;
    AImage *image;
    media_status_t status = AImageReader_acquireNextImage(_imageReader, &image);
    if (status != AMEDIA_OK) {
        return nullptr;
    }
    return image;
}

void Camera::deleteImage(AImage *image) {
    if (image) {
        AImage_delete(image);
    }
}

int Camera::displayImage(AImage *image, ANativeWindow_Buffer *buf) {
    if (image) {
        int32_t srcFormat = -1;
        AImage_getFormat(image, &srcFormat);
        int32_t srcPlanes = 0;
        AImage_getNumberOfPlanes(image, &srcPlanes);
        switch (0) {
            case 0:
                PresentImage(buf, image);
                break;
            case 90:
                PresentImage90(buf, image);
                break;
            case 180:
                PresentImage180(buf, image);
                break;
            case 270:
                PresentImage270(buf, image);
                break;
            default:
//                ASSERT(0, "NOT recognized display rotation: %d", 0);
                break;
        }

    }
    return 0;
}

/*
 * PresentImage()
 *   Converting yuv to RGB
 *   No rotation: (x,y) --> (x, y)
 *   Refer to:
 * https://mathbits.com/MathBits/TISection/Geometry/Transformations2.htm
 */
void Camera::PresentImage(ANativeWindow_Buffer *buf, AImage *image) {
    AImageCropRect srcRect;
    AImage_getCropRect(image, &srcRect);

    int32_t yStride, uvStride;
    uint8_t *yPixel, *uPixel, *vPixel;
    int32_t yLen, uLen, vLen;
    AImage_getPlaneRowStride(image, 0, &yStride);
    AImage_getPlaneRowStride(image, 1, &uvStride);
    AImage_getPlaneData(image, 0, &yPixel, &yLen);
    AImage_getPlaneData(image, 1, &vPixel, &vLen);
    AImage_getPlaneData(image, 2, &uPixel, &uLen);
    int32_t uvPixelStride;
    AImage_getPlanePixelStride(image, 1, &uvPixelStride);

    int32_t height = MIN(buf->height, (srcRect.bottom - srcRect.top));
    int32_t width = MIN(buf->width, (srcRect.right - srcRect.left));

    uint32_t *out = static_cast<uint32_t *>(buf->bits);
    for (int32_t y = 0; y < height; y++) {
        const uint8_t *pY = yPixel + yStride * (y + srcRect.top) + srcRect.left;

        int32_t uv_row_start = uvStride * ((y + srcRect.top) >> 1);
        const uint8_t *pU = uPixel + uv_row_start + (srcRect.left >> 1);
        const uint8_t *pV = vPixel + uv_row_start + (srcRect.left >> 1);

        for (int32_t x = 0; x < width; x++) {
            const int32_t uv_offset = (x >> 1) * uvPixelStride;
            out[x] = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
        }
        out += buf->stride;
    }
}

/*
 * PresentImage90()
 *   Converting YUV to RGB
 *   Rotation image anti-clockwise 90 degree -- (x, y) --> (-y, x)
 */
void Camera::PresentImage90(ANativeWindow_Buffer *buf, AImage *image) {
    AImageCropRect srcRect;
    AImage_getCropRect(image, &srcRect);

    int32_t yStride, uvStride;
    uint8_t *yPixel, *uPixel, *vPixel;
    int32_t yLen, uLen, vLen;
    AImage_getPlaneRowStride(image, 0, &yStride);
    AImage_getPlaneRowStride(image, 1, &uvStride);
    AImage_getPlaneData(image, 0, &yPixel, &yLen);
    AImage_getPlaneData(image, 1, &vPixel, &vLen);
    AImage_getPlaneData(image, 2, &uPixel, &uLen);
    int32_t uvPixelStride;
    AImage_getPlanePixelStride(image, 1, &uvPixelStride);

    int32_t height = MIN(buf->width, (srcRect.bottom - srcRect.top));
    int32_t width = MIN(buf->height, (srcRect.right - srcRect.left));

    uint32_t *out = static_cast<uint32_t *>(buf->bits);
    out += height - 1;
    for (int32_t y = 0; y < height; y++) {
        const uint8_t *pY = yPixel + yStride * (y + srcRect.top) + srcRect.left;

        int32_t uv_row_start = uvStride * ((y + srcRect.top) >> 1);
        const uint8_t *pU = uPixel + uv_row_start + (srcRect.left >> 1);
        const uint8_t *pV = vPixel + uv_row_start + (srcRect.left >> 1);

        for (int32_t x = 0; x < width; x++) {
            const int32_t uv_offset = (x >> 1) * uvPixelStride;
            // [x, y]--> [-y, x]
            out[x * buf->stride] = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
        }
        out -= 1;  // move to the next column
    }
}

/*
 * PresentImage180()
 *   Converting yuv to RGB
 *   Rotate image 180 degree: (x, y) --> (-x, -y)
 */
void Camera::PresentImage180(ANativeWindow_Buffer *buf, AImage *image) {
    AImageCropRect srcRect;
    AImage_getCropRect(image, &srcRect);

    int32_t yStride, uvStride;
    uint8_t *yPixel, *uPixel, *vPixel;
    int32_t yLen, uLen, vLen;
    AImage_getPlaneRowStride(image, 0, &yStride);
    AImage_getPlaneRowStride(image, 1, &uvStride);
    AImage_getPlaneData(image, 0, &yPixel, &yLen);
    AImage_getPlaneData(image, 1, &vPixel, &vLen);
    AImage_getPlaneData(image, 2, &uPixel, &uLen);
    int32_t uvPixelStride;
    AImage_getPlanePixelStride(image, 1, &uvPixelStride);

    int32_t height = MIN(buf->height, (srcRect.bottom - srcRect.top));
    int32_t width = MIN(buf->width, (srcRect.right - srcRect.left));

    uint32_t *out = static_cast<uint32_t *>(buf->bits);
    out += (height - 1) * buf->stride;
    for (int32_t y = 0; y < height; y++) {
        const uint8_t *pY = yPixel + yStride * (y + srcRect.top) + srcRect.left;

        int32_t uv_row_start = uvStride * ((y + srcRect.top) >> 1);
        const uint8_t *pU = uPixel + uv_row_start + (srcRect.left >> 1);
        const uint8_t *pV = vPixel + uv_row_start + (srcRect.left >> 1);

        for (int32_t x = 0; x < width; x++) {
            const int32_t uv_offset = (x >> 1) * uvPixelStride;
            // mirror image since we are using front camera
            out[width - 1 - x] = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
            // out[x] = YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
        }
        out -= buf->stride;
    }
}

/*
 * PresentImage270()
 *   Converting image from YUV to RGB
 *   Rotate Image counter-clockwise 270 degree: (x, y) --> (y, x)
 */
void Camera::PresentImage270(ANativeWindow_Buffer *buf, AImage *image) {
    AImageCropRect srcRect;
    AImage_getCropRect(image, &srcRect);

    int32_t yStride, uvStride;
    uint8_t *yPixel, *uPixel, *vPixel;
    int32_t yLen, uLen, vLen;
    AImage_getPlaneRowStride(image, 0, &yStride);
    AImage_getPlaneRowStride(image, 1, &uvStride);
    AImage_getPlaneData(image, 0, &yPixel, &yLen);
    AImage_getPlaneData(image, 1, &vPixel, &vLen);
    AImage_getPlaneData(image, 2, &uPixel, &uLen);
    int32_t uvPixelStride;
    AImage_getPlanePixelStride(image, 1, &uvPixelStride);

    int32_t height = MIN(buf->width, (srcRect.bottom - srcRect.top));
    int32_t width = MIN(buf->height, (srcRect.right - srcRect.left));

    uint32_t *out = static_cast<uint32_t *>(buf->bits);
    for (int32_t y = 0; y < height; y++) {
        const uint8_t *pY = yPixel + yStride * (y + srcRect.top) + srcRect.left;

        int32_t uv_row_start = uvStride * ((y + srcRect.top) >> 1);
        const uint8_t *pU = uPixel + uv_row_start + (srcRect.left >> 1);
        const uint8_t *pV = vPixel + uv_row_start + (srcRect.left >> 1);

        for (int32_t x = 0; x < width; x++) {
            const int32_t uv_offset = (x >> 1) * uvPixelStride;
            out[(width - 1 - x) * buf->stride] =
                    YUV2RGB(pY[x], pU[uv_offset], pV[uv_offset]);
        }
        out += 1;  // move to the next column
    }
}


AImageReader *Camera::createYuvReader() {
    AImageReader_new(
            640, 480,
            MAX_BUF_COUNT,
            AIMAGE_FORMAT_YUV_420_888,
            &_imageReader
    );

    AImageReader_ImageListener listener{
            .context = this,
            .onImageAvailable = imageCallback,
    };
    AImageReader_setImageListener(_imageReader, &listener);
    return _imageReader;
}


AImageReader *Camera::createJpegReader() {
    AImageReader *reader = nullptr;
    media_status_t status = AImageReader_new(
            640, 480,
            AIMAGE_FORMAT_JPEG,
            4,
            &reader
    );

    //if (status != AMEDIA_OK)
    // Handle errors here

    AImageReader_ImageListener listener{
            .context = _app,
            .onImageAvailable = imageCallback,
    };

    AImageReader_setImageListener(reader, &listener);

    return reader;
}

// CaptureSession state callbacks
void OnSessionClosed(void* ctx, ACameraCaptureSession* ses) {
    LOGW("session %p closed", ses);
    reinterpret_cast<Camera*>(ctx)->OnSessionState(ses, 0);
}
void OnSessionReady(void* ctx, ACameraCaptureSession* ses) {
    LOGW("session %p ready", ses);
    reinterpret_cast<Camera*>(ctx)->OnSessionState(ses, 1);
}
void OnSessionActive(void* ctx, ACameraCaptureSession* ses) {
    LOGW("session %p active", ses);
    reinterpret_cast<Camera*>(ctx)->OnSessionState(ses, 2);
}

/**
 * Handles capture session state changes.
 *   Update into internal session state.
 */
void Camera::OnSessionState(ACameraCaptureSession* ses,
                               int state) {
    if (!ses || ses != captureSession_) {
        LOGW("CaptureSession is %s", (ses ? "NOT our session" : "NULL"));
        return;
    }
}

ACameraCaptureSession_stateCallbacks* Camera::GetSessionListener() {
    static ACameraCaptureSession_stateCallbacks sessionListener = {
            .context = this,
            .onClosed = ::OnSessionClosed,
            .onReady = ::OnSessionReady,
            .onActive = ::OnSessionActive,
    };
    return &sessionListener;
}

void Camera::CreateSession(ANativeWindow* previewWindow, int32_t imageRotation) {
    // Create output from this app's ANativeWindow, and add into output container
    ACaptureSessionOutput* sessionOutput_ = nullptr;
    ACameraOutputTarget* target_ = nullptr;
    ACaptureRequest* request_ = nullptr;

    ACaptureSessionOutputContainer_create(&outputContainer_);
    // 锁定屏幕防止被其它程序刷新导致图像混乱。
    ANativeWindow_acquire(previewWindow);
    ACaptureSessionOutput_create(previewWindow, &sessionOutput_);
    ACaptureSessionOutputContainer_add(outputContainer_, sessionOutput_);
    ACameraOutputTarget_create(previewWindow, &target_);
    ACameraDevice_createCaptureRequest(_cameraDevice, TEMPLATE_PREVIEW, &request_);
    ACaptureRequest_addTarget(request_, target_);

    // Create a capture session for the given preview request
    ACameraDevice_createCaptureSession(_cameraDevice, outputContainer_, GetSessionListener(), &captureSession_);


    /*
     * Only preview request is in manual mode, JPG is always in Auto mode
     * JPG capture mode could also be switch into manual mode and control
     * the capture parameters, this sample leaves JPG capture to be auto mode
     * (auto control has better effect than author's manual control)
     */
    uint8_t aeModeOff = ACAMERA_CONTROL_AE_MODE_OFF;
    ACaptureRequest_setEntry_u8(request_,ACAMERA_CONTROL_AE_MODE, 1, &aeModeOff);
    ACaptureRequest_setEntry_i32(request_,
                              ACAMERA_SENSOR_SENSITIVITY, 1, &sensitivity_);
    ACaptureRequest_setEntry_i64(request_,ACAMERA_SENSOR_EXPOSURE_TIME, 1, &exposureTime_);
    StartPreview(true, &request_);
}


void Camera::StartPreview(bool start, ACaptureRequest** request) {
    if (start) {
        ACameraCaptureSession_setRepeatingRequest(captureSession_, nullptr, 1, request, nullptr);
    } else {
        ACameraCaptureSession_stopRepeating(captureSession_);
    }
}

