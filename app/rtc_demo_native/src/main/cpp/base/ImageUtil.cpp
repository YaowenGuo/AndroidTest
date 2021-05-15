//
// Created by Albert on 2021/5/15.
//

#include "ImageUtil.h"


ANativeWindow *createWindow(AImageReader *reader) {
    ANativeWindow *nativeWindow;
    AImageReader_getWindow(reader, &nativeWindow);

    return nativeWindow;
}