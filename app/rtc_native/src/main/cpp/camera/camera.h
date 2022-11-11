//
// Created by Albert on 2022/11/11.
//

#ifndef ANDROIDTEST_CAMERA_H
#define ANDROIDTEST_CAMERA_H

#include <camera/NdkCaptureRequest.h>
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraCaptureSession.h>

#include "utils/native_debug.h"
#include "image_reader.h"

using namespace std;
// helper classes to hold enumerated camera
class CameraInfo {
public:
    ACameraDevice *device_;
    std::string id_;
    acamera_metadata_enum_android_lens_facing_t facing_;
    bool available_;  // free to use ( no other apps are using
    bool owner_;      // we are the owner of the camera
    explicit CameraInfo(const char *id)
            : id_(id),
              device_(nullptr),
              facing_(ACAMERA_LENS_FACING_FRONT),
              available_(false),
              owner_(false) {
    }
};

template<typename T>
class RangeValue {
public:
    T min_, max_;


    /**
     * return absolute value from relative value
     * value: in percent (50 for 50%)
     */
    T value(int percent) {
        return static_cast<T>(min_ + (max_ - min_) * percent / 100);
    }


    RangeValue() { min_ = max_ = static_cast<T>(0); }


    bool Supported(void) const { return (min_ != max_); }
};


enum class CaptureSessionState : int32_t {
    READY = 0,  // session is ready
    ACTIVE,     // session is busy
    CLOSED,     // session is closed(by itself or a new session evicts)
    MAX_STATE
};


enum PREVIEW_INDICES {
    PREVIEW_REQUEST_IDX = 0,
    JPG_CAPTURE_REQUEST_IDX,
    CAPTURE_REQUEST_COUNT,
};

struct CaptureRequestInfo {
    ANativeWindow *outputNativeWindow_;
    ACaptureSessionOutput *sessionOutput_;
    ACameraOutputTarget *target_;
    ACaptureRequest *request_;
    ACameraDevice_request_template template_;
    int sessionSequenceId_;
};

class Camera {
public:
    Camera(std::weak_ptr<ACameraManager> camera_mgr, CameraInfo& camera_id);


    ~Camera();


    bool MatchCaptureSizeRequest(ANativeWindow *display, ImageFormat *view,
                                 ImageFormat *capture);


    void CreateSession(ANativeWindow *previewWindow, ANativeWindow *jpgWindow,
                       int32_t imageRotation);


    bool GetSensorOrientation(int32_t *facing, int32_t *angle);


    void OnSessionState(ACameraCaptureSession *ses, CaptureSessionState state);


    void OnCaptureSequenceEnd(ACameraCaptureSession *session, int sequenceId,
                              int64_t frameNumber);


    void OnCaptureFailed(ACameraCaptureSession *session, ACaptureRequest *request,
                         ACameraCaptureFailure *failure);


    void StartPreview(bool start);


    void StartCapture(rtc::scoped_refptr<rtc_demo::AndroidVideoTrackSource> source);


    bool TakePhoto();


    bool GetExposureRange(int64_t *min, int64_t *max, int64_t *curVal);


    bool GetSensitivityRange(int64_t *min, int64_t *max, int64_t *curVal);


    void UpdateCameraRequestParameter(int32_t code, int64_t val);


    void OnPhotoTaken(const char *fileName);


    weak_ptr<ImageReader> GetYuvReader();


    weak_ptr<ImageReader> GetJpgReader();

private:
    std::weak_ptr<ACameraManager> camera_mgr_;
    CameraInfo cameraInfo_;
    std::shared_ptr<ImageReader> yuv_reader_;
    std::shared_ptr<ImageReader> jpg_reader_;
//    uint32_t cameraFacing_;
    uint32_t cameraOrientation_;

    std::vector<CaptureRequestInfo> requests_;

    ACaptureSessionOutputContainer *outputContainer_;
    ACameraCaptureSession *captureSession_{};
    CaptureSessionState captureSessionState_;

    // set up exposure control
    int64_t exposureTime_;
    RangeValue<int64_t> exposureRange_;
    int32_t sensitivity_;
    RangeValue<int32_t> sensitivityRange_;
    volatile bool valid_;


    ACameraDevice_stateCallbacks *GetDeviceListener();


    ACameraCaptureSession_stateCallbacks *GetSessionListener();


    ACameraCaptureSession_captureCallbacks *GetCaptureCallback();


    ACameraManager_AvailabilityCallbacks *GetManagerListener();
};

#endif //ANDROIDTEST_CAMERA_H
