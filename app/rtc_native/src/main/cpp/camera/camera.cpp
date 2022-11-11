//
// Created by Albert on 2022/11/11.
//

#include <camera/NdkCameraManager.h>
#include <utility>
#include "camera.h"
#include "main/window_monitor.h"
#include "utils/camera_utils.h"

/**
 * Range of Camera Exposure Time:
 *     Camera's capability range have a very long range which may be disturbing
 *     on camera. For this sample purpose, clamp to a range showing visible
 *     video on preview: 100000ns ~ 250000000ns
 */
static const uint64_t kMinExposureTime = static_cast<uint64_t>(1000000);
static const uint64_t kMaxExposureTime = static_cast<uint64_t>(250000000);


/**
 * A helper class to assist image size comparison, by comparing the absolute
 * size
 * regardless of the portrait or landscape mode.
 */
class DisplayDimension {
public:
    DisplayDimension(int32_t w, int32_t h) : w_(w), h_(h), portrait_(false) {
        if (h > w) {
            // make it landscape
            w_ = h;
            h_ = w;
            portrait_ = true;
        }
    }


    DisplayDimension(const DisplayDimension &other) {
        w_ = other.w_;
        h_ = other.h_;
        portrait_ = other.portrait_;
    }


    DisplayDimension() {
        w_ = 0;
        h_ = 0;
        portrait_ = false;
    }


    DisplayDimension &operator=(const DisplayDimension &other) = default;


    bool IsSameRatio(DisplayDimension &other) const {
        return (w_ * other.h_ == h_ * other.w_);
    }


    bool operator>(DisplayDimension &other) const {
        return (w_ >= other.w_ & h_ >= other.h_);
    }


    bool operator==(DisplayDimension &other) const {
        return (w_ == other.w_ && h_ == other.h_ && portrait_ == other.portrait_);
    }


    DisplayDimension operator-(DisplayDimension &other) const {
        DisplayDimension delta(w_ - other.w_, h_ - other.h_);
        return delta;
    }


    void Flip() { portrait_ = !portrait_; }


    bool IsPortrait() const { return portrait_; }


    int32_t width() const { return w_; }


    int32_t height() const { return h_; }


    int32_t org_width() { return (portrait_ ? h_ : w_); }


    int32_t org_height() { return (portrait_ ? w_ : h_); }


private:
    int32_t w_, h_;
    bool portrait_;
};


Camera::Camera(std::weak_ptr<ACameraManager> camera_mgr, CameraInfo& cameraInfo)
        : camera_mgr_(std::move(camera_mgr)),
          cameraInfo_(std::move(cameraInfo)),
          cameraOrientation_(0),
          outputContainer_(nullptr),
          captureSessionState_(CaptureSessionState::MAX_STATE),
          exposureTime_(static_cast<int64_t>(0)) {
    valid_ = false;
    auto camera_manager = camera_mgr_.lock();
    ASSERT(camera_manager, "ACameraManager is null");

    requests_.resize(CAPTURE_REQUEST_COUNT);
    memset(requests_.data(), 0, requests_.size() * sizeof(requests_[0]));

    ASSERT(cameraInfo_.id_.size(), "Unknown ActiveCameraIdx");

    // up value of 2% * range + min as starting value (just a number, no magic)
    ACameraMetadata *metadataObj;
    CALL_MGR(getCameraCharacteristics(camera_manager.get(), cameraInfo_.id_.c_str(), &metadataObj));
    ACameraMetadata_const_entry val = {0,};
    camera_status_t status = ACameraMetadata_getConstEntry(
            metadataObj, ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE, &val);
    if (status == ACAMERA_OK) {
        exposureRange_.min_ = val.data.i64[0];
        if (exposureRange_.min_ < kMinExposureTime) {
            exposureRange_.min_ = kMinExposureTime;
        }
        exposureRange_.max_ = val.data.i64[1];
        if (exposureRange_.max_ > kMaxExposureTime) {
            exposureRange_.max_ = kMaxExposureTime;
        }
        exposureTime_ = exposureRange_.value(2);
    } else {
        LOGW("Unsupported ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE");
        exposureRange_.min_ = exposureRange_.max_ = 0l;
        exposureTime_ = 0l;
    }
    status = ACameraMetadata_getConstEntry(
            metadataObj, ACAMERA_SENSOR_INFO_SENSITIVITY_RANGE, &val);

    if (status == ACAMERA_OK) {
        sensitivityRange_.min_ = val.data.i32[0];
        sensitivityRange_.max_ = val.data.i32[1];

        sensitivity_ = sensitivityRange_.value(2);
    } else {
        LOGW("failed for ACAMERA_SENSOR_INFO_SENSITIVITY_RANGE");
        sensitivityRange_.min_ = sensitivityRange_.max_ = 0;
        sensitivity_ = 0;
    }
    valid_ = true;
}


/**
 * Create a camera object for onboard BACK_FACING camera
 */
void Camera::StartCapture(rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource> source) {
    auto camera_manager = camera_mgr_.lock();
    ASSERT(camera_manager, "ACameraManager is null");
    // Create back facing camera device
    CALL_MGR(openCamera(camera_manager.get(), cameraInfo_.id_.c_str(), GetDeviceListener(),
                        &cameraInfo_.device_));

    auto rotation = WindowMonitor::GetInstance()->GetDisplayRotation();

    int32_t facing = 0, angle = 0, imageRotation = 0;
    if (GetSensorOrientation(&facing, &angle)) {
        if (facing == ACAMERA_LENS_FACING_FRONT) {
            imageRotation = (angle + rotation) % 360;
            imageRotation = (360 - imageRotation) % 360;
        } else {
            imageRotation = (angle - rotation + 360) % 360;
        }
    }
    LOGI("Phone Rotation: %d, Present Rotation Angle: %d", rotation, imageRotation);
    ImageFormat view{0, 0, 0}, capture{0, 0, 0};
    auto window = WindowMonitor::GetInstance();
    MatchCaptureSizeRequest(window->App()->window, &view, &capture);

    ASSERT(view.width && view.height, "Could not find supportable resolution");

    // Request the necessary nativeWindow to OS
    bool portraitNativeWindow = (window->WindowWidth() < window->WindowHeight());

    yuv_reader_ = make_shared<ImageReader>(&view, AIMAGE_FORMAT_YUV_420_888);
    yuv_reader_->SetPresentRotation(imageRotation);
    jpg_reader_ = make_shared<ImageReader>(&capture, AIMAGE_FORMAT_JPEG);
    jpg_reader_->SetPresentRotation(imageRotation);
    jpg_reader_->RegisterCallback(this, [this](void *ctx, const char *str) -> void {
        reinterpret_cast<Camera * >(ctx)->OnPhotoTaken(str);
    });
    yuv_reader_->SetVideoSource(source);
    // now we could create session
    CreateSession(yuv_reader_->GetNativeWindow(),
                  jpg_reader_->GetNativeWindow(), imageRotation);

    CALL_SESSION(setRepeatingRequest(captureSession_, nullptr, 1,
                                     &requests_[PREVIEW_REQUEST_IDX].request_,
                                     nullptr));

    StartPreview(true);
}


/**
 * Find a compatible camera modes:
 *    1) the same aspect ration as the native display window, which should be a
 *       rotated version of the physical device
 *    2) the smallest resolution in the camera mode list
 * This is to minimize the later color space conversion workload.
 */
bool Camera::MatchCaptureSizeRequest(ANativeWindow *display,
                                     ImageFormat *resView,
                                     ImageFormat *resCap) {
    DisplayDimension disp(ANativeWindow_getWidth(display),
                          ANativeWindow_getHeight(display));
    if (cameraOrientation_ == 90 || cameraOrientation_ == 270) {
        disp.Flip();
    }
    auto camera_manager = camera_mgr_.lock();
    ASSERT(camera_manager, "ACameraManager is null");
    ACameraMetadata *metadata;
    CALL_MGR(getCameraCharacteristics(camera_manager.get(), cameraInfo_.id_.c_str(), &metadata));
    ACameraMetadata_const_entry entry;
    CALL_METADATA(getConstEntry(metadata, ACAMERA_SCALER_AVAILABLE_STREAM_CONFIGURATIONS, &entry));
    // format of the data: format, width, height, input?, type int32
    bool foundIt = false;
    DisplayDimension foundRes(4000, 4000);
    DisplayDimension maxJPG(0, 0);

    for (int i = 0; i < entry.count; i += 4) {
        int32_t input = entry.data.i32[i + 3];
        int32_t format = entry.data.i32[i + 0];
        if (input) continue;

        if (format == AIMAGE_FORMAT_YUV_420_888 || format == AIMAGE_FORMAT_JPEG) {
            DisplayDimension res(entry.data.i32[i + 1],
                                 entry.data.i32[i + 2]);
            if (!disp.IsSameRatio(res)) continue;
            if (format == AIMAGE_FORMAT_YUV_420_888 && foundRes > res) {
                foundIt = true;
                foundRes = res;
            } else if (format == AIMAGE_FORMAT_JPEG && res > maxJPG) {
                maxJPG = res;
            }
        }
    }

    if (foundIt) {
        resView->width = foundRes.org_width();
        resView->height = foundRes.org_height();
        resCap->width = maxJPG.org_width();
        resCap->height = maxJPG.org_height();
    } else {
        LOGW("Did not find any compatible camera resolution, taking 640x480");
        if (disp.IsPortrait()) {
            resView->width = 480;
            resView->height = 640;
        } else {
            resView->width = 640;
            resView->height = 480;
        }
        *resCap = *resView;
    }
    resView->format = AIMAGE_FORMAT_YUV_420_888;
    resCap->format = AIMAGE_FORMAT_JPEG;
    return foundIt;
}


void Camera::CreateSession(ANativeWindow *previewWindow,
                           ANativeWindow *jpgWindow, int32_t imageRotation) {
    // Create output from this app's ANativeWindow, and add into output container
    requests_[PREVIEW_REQUEST_IDX].outputNativeWindow_ = previewWindow;
    requests_[PREVIEW_REQUEST_IDX].template_ = TEMPLATE_PREVIEW;
    requests_[JPG_CAPTURE_REQUEST_IDX].outputNativeWindow_ = jpgWindow;
    requests_[JPG_CAPTURE_REQUEST_IDX].template_ = TEMPLATE_STILL_CAPTURE;

    CALL_CONTAINER(create(&outputContainer_));
    for (auto &req: requests_) {
        ANativeWindow_acquire(req.outputNativeWindow_);
        CALL_OUTPUT(create(req.outputNativeWindow_, &req.sessionOutput_));
        CALL_CONTAINER(add(outputContainer_, req.sessionOutput_));
        CALL_TARGET(create(req.outputNativeWindow_, &req.target_));
        CALL_DEV(createCaptureRequest(cameraInfo_.device_,
                                      req.template_, &req.request_));
        CALL_REQUEST(addTarget(req.request_, req.target_));
    }

    // Create a capture session for the given preview request
    captureSessionState_ = CaptureSessionState::READY;
    CALL_DEV(createCaptureSession(cameraInfo_.device_,
                                  outputContainer_, GetSessionListener(),
                                  &captureSession_));

    ACaptureRequest_setEntry_i32(requests_[JPG_CAPTURE_REQUEST_IDX].request_,
                                 ACAMERA_JPEG_ORIENTATION, 1, &imageRotation);

    /*
     * Only preview request is in manual mode, JPG is always in Auto mode
     * JPG capture mode could also be switch into manual mode and control
     * the capture parameters, this sample leaves JPG capture to be auto mode
     * (auto control has better effect than author's manual control)
     */
    uint8_t aeModeOff = ACAMERA_CONTROL_AE_MODE_OFF;
    CALL_REQUEST(setEntry_u8(requests_[PREVIEW_REQUEST_IDX].request_,
                             ACAMERA_CONTROL_AE_MODE, 1, &aeModeOff));
    CALL_REQUEST(setEntry_i32(requests_[PREVIEW_REQUEST_IDX].request_,
                              ACAMERA_SENSOR_SENSITIVITY, 1, &sensitivity_));
    CALL_REQUEST(setEntry_i64(requests_[PREVIEW_REQUEST_IDX].request_,
                              ACAMERA_SENSOR_EXPOSURE_TIME, 1, &exposureTime_));
}


Camera::~Camera() {
    valid_ = false;
    // stop session if it is on:
    if (captureSessionState_ == CaptureSessionState::ACTIVE) {
        ACameraCaptureSession_stopRepeating(captureSession_);
    }
    ACameraCaptureSession_close(captureSession_);

    for (auto &req: requests_) {
        CALL_REQUEST(removeTarget(req.request_, req.target_));
        ACaptureRequest_free(req.request_);
        ACameraOutputTarget_free(req.target_);

        CALL_CONTAINER(remove(outputContainer_, req.sessionOutput_));
        ACaptureSessionOutput_free(req.sessionOutput_);

        ANativeWindow_release(req.outputNativeWindow_);
    }

    requests_.resize(0);
    ACaptureSessionOutputContainer_free(outputContainer_);
}


/**
 * GetSensorOrientation()
 *     Retrieve current sensor orientation regarding to the phone device
 * orientation
 *     SensorOrientation is NOT settable.
 */
bool Camera::GetSensorOrientation(int32_t *facing, int32_t *angle) {
    auto camera_manager = camera_mgr_.lock();
    ASSERT(camera_manager, "ACameraManager is null");

    ACameraMetadata *metadataObj;
    ACameraMetadata_const_entry face, orientation;
    CALL_MGR(getCameraCharacteristics(camera_manager.get(), cameraInfo_.id_.c_str(), &metadataObj));
    CALL_METADATA(getConstEntry(metadataObj, ACAMERA_LENS_FACING, &face));

    CALL_METADATA(
            getConstEntry(metadataObj, ACAMERA_SENSOR_ORIENTATION, &orientation));

    LOGI("====Current SENSOR_ORIENTATION: %8d", orientation.data.i32[0]);

    ACameraMetadata_free(metadataObj);
    cameraOrientation_ = orientation.data.i32[0];

    if (facing) *facing = static_cast<int32_t>(face.data.u8[0]);
    if (angle) *angle = cameraOrientation_;
    return true;
}


/**
 * StartPreview()
 *   Toggle preview start/stop
 */
void Camera::StartPreview(bool start) {
    if (start) {
        CALL_SESSION(setRepeatingRequest(captureSession_, nullptr, 1,
                                         &requests_[PREVIEW_REQUEST_IDX].request_,
                                         nullptr));
    } else if (!start && captureSessionState_ == CaptureSessionState::ACTIVE) {
        ACameraCaptureSession_stopRepeating(captureSession_);
    } else {
        ASSERT(false, "Conflict states(%s, %d)", (start ? "true" : "false"),
               captureSessionState_);
    }
}


/**
 * Capture one jpg photo into
 *     /sdcard/DCIM/Camera
 * refer to WriteFile() for details
 */
bool Camera::TakePhoto() {
    if (captureSessionState_ == CaptureSessionState::ACTIVE) {
        ACameraCaptureSession_stopRepeating(captureSession_);
    }

    CALL_SESSION(capture(captureSession_, GetCaptureCallback(), 1,
                         &requests_[JPG_CAPTURE_REQUEST_IDX].request_,
                         &requests_[JPG_CAPTURE_REQUEST_IDX].sessionSequenceId_));
    return true;
}


void Camera::UpdateCameraRequestParameter(int32_t code, int64_t val) {
    ACaptureRequest *request = requests_[PREVIEW_REQUEST_IDX].request_;
    switch (code) {
        case ACAMERA_SENSOR_EXPOSURE_TIME:
            if (exposureRange_.Supported()) {
                exposureTime_ = val;
                CALL_REQUEST(setEntry_i64(request, ACAMERA_SENSOR_EXPOSURE_TIME, 1,
                                          &exposureTime_));
            }
            break;

        case ACAMERA_SENSOR_SENSITIVITY:
            if (sensitivityRange_.Supported()) {
                sensitivity_ = val;
                CALL_REQUEST(
                        setEntry_i32(request, ACAMERA_SENSOR_SENSITIVITY, 1, &sensitivity_));
            }
            break;
        default:
            ASSERT(false, "==ERROR==: error code for CameraParameterChange: %d",
                   code);
            return;
    }

    uint8_t aeModeOff = ACAMERA_CONTROL_AE_MODE_OFF;
    CALL_REQUEST(setEntry_u8(request, ACAMERA_CONTROL_AE_MODE, 1, &aeModeOff));
    CALL_SESSION(
            setRepeatingRequest(captureSession_, nullptr, 1, &request,
                                &requests_[PREVIEW_REQUEST_IDX].sessionSequenceId_));
}


/**
 * Retrieve Camera Exposure adjustable range.
 *
 * @param min Camera minimium exposure time in nanoseconds
 * @param max Camera maximum exposure tiem in nanoseconds
 *
 * @return true  min and max are loaded with the camera's exposure values
 *         false camera has not initialized, no value available
 */
bool Camera::GetExposureRange(int64_t *min, int64_t *max, int64_t *curVal) {
    if (!exposureRange_.Supported() || !exposureTime_ || !min || !max || !curVal) {
        return false;
    }
    *min = exposureRange_.min_;
    *max = exposureRange_.max_;
    *curVal = exposureTime_;

    return true;
}


/**
 * Retrieve Camera sensitivity range.
 *
 * @param min Camera minimium sensitivity
 * @param max Camera maximum sensitivity
 *
 * @return true  min and max are loaded with the camera's sensitivity values
 *         false camera has not initialized, no value available
 */
bool Camera::GetSensitivityRange(int64_t *min, int64_t *max,
                                 int64_t *curVal) {
    if (!sensitivityRange_.Supported() || !sensitivity_ || !min || !max || !curVal) {
        return false;
    }
    *min = static_cast<int64_t>(sensitivityRange_.min_);
    *max = static_cast<int64_t>(sensitivityRange_.max_);
    *curVal = sensitivity_;
    return true;
}


void Camera::OnPhotoTaken(const char *fileName) {
    JNIEnv *jni;

    auto activity = WindowMonitor::GetInstance()->App()->activity;
    activity->vm->AttachCurrentThread(&jni, nullptr);
    // Default class retrieval
    jclass clazz = jni->GetObjectClass(activity->clazz);
    jmethodID methodID = jni->GetMethodID(clazz, "OnPhotoTaken", "(Ljava/lang/String;)V");
    jstring javaName = jni->NewStringUTF(fileName);

    jni->CallVoidMethod(activity->clazz, methodID, javaName);
    activity->vm->DetachCurrentThread();
}


weak_ptr<ImageReader> Camera::GetJpgReader() {
    return jpg_reader_;
}


weak_ptr<ImageReader> Camera::GetYuvReader() {
    return yuv_reader_;
}