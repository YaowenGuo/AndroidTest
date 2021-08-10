#include "attach_thread_scoped.h"

#include "native_debug.h"

namespace rtc_demo {

    AttachThreadScoped::AttachThreadScoped(JavaVM *jvm)
            : attached_(false), jvm_(jvm), env_(nullptr) {
        jint ret_val = jvm->GetEnv(reinterpret_cast<void **>(&env_), JNI_VERSION_1_6);
        if (ret_val == JNI_EDETACHED) {
            // Attach the thread to the Java VM.
            ret_val = jvm_->AttachCurrentThread(&env_, nullptr);
            attached_ = ret_val == JNI_OK;
            assert(attached_);
        }
    }

    AttachThreadScoped::~AttachThreadScoped() {
        if (attached_ && (jvm_->DetachCurrentThread() < 0)) {
            assert(false);
        }
    }

    JNIEnv *AttachThreadScoped::env() { return env_; }

}  // namespace truman_client
