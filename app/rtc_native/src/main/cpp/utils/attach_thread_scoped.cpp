#include <assert.h>
#include "attach_thread_scoped.h"

#include "native_debug.h"
#include "utils/jvm.h"

namespace rtc_demo {

    AttachThreadScoped::AttachThreadScoped(JNIEnv *env)
            : attached_(false), env_(nullptr) {
        jni::GetJVM()->GetEnv(reinterpret_cast<void **>(&env_), JNI_VERSION_1_6);
        auto ret_val = jni::GetJVM()->AttachCurrentThread(&env_, nullptr);
        ASSERT(ret_val == JNI_OK, "Attach Current Thread Failed!.")
    }

    AttachThreadScoped::~AttachThreadScoped() {
        if (attached_ && (jvm_->DetachCurrentThread() < 0)) {
            assert(false);
        }
    }

    JNIEnv *AttachThreadScoped::env() { return env_; }

}  // namespace truman_client
