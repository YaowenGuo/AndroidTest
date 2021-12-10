#ifndef COMMON_ATTACH_THREAD_SCOPED_H_
#define COMMON_ATTACH_THREAD_SCOPED_H_

#include <jni.h>
#include "jvm.h"

namespace rtc_demo {

// Attach thread to JVM if necessary and detach at scope end if originally
// attached.
    class AttachThreadScoped {
    public:
        explicit AttachThreadScoped(JNIEnv *env);

        ~AttachThreadScoped();

        JNIEnv *env();

    private:
        bool attached_;
        JavaVM *jvm_;
        JNIEnv *env_;
    };

}  // namespace truman_client

#endif  // COMMON_ATTACH_THREAD_SCOPED_H_
