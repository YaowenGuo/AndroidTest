//
// Created by Albert on 2021/5/15.
//

#ifndef ANDROIDTEST_IMAGEUTIL_H
#define ANDROIDTEST_IMAGEUTIL_H

#include <media/NdkImageReader.h>
#include <android/native_window.h>


ANativeWindow *createWindow(AImageReader *reader);


#endif //ANDROIDTEST_IMAGEUTIL_H
