#include <android/log.h>
#include <sstream>

#define LOG_TAG "RTC-DEMO"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ASSERT(cond, fmt, ...)                                \
  if (!(cond)) {                                              \
    __android_log_assert(#cond, LOG_TAG, fmt, ##__VA_ARGS__); \
  }


#define THREAD_CURRENT(name)               \
  std::ostringstream oss;                  \
  oss << std::this_thread::get_id();       \
  LOGD("Current Thread: id = %s, tag = %s", oss.str().c_str(), name)