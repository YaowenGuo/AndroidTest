//
// Created by Albert on 2021/5/11.
//

#ifndef ANDROIDTEST_PERMISSION_H
#define ANDROIDTEST_PERMISSION_H


#include <jni.h>

class Permission {
    static void RequestCameraPermission();

public:
    static void PermissionResult(JNIEnv *jni, jobject j_live, bool granted, jobject context);
};


#endif //ANDROIDTEST_PERMISSION_H
