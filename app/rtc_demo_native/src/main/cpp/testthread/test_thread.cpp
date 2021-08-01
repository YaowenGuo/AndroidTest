//
// Created by Albert on 2021/7/13.
//

#include <rtc_base/platform_thread.h>
#include <absl/base/config.h>

#include <iostream>
#include "test_thread.h"
#include "utils/native_debug.h"

#undef ABSL_HAVE_STD_STRING_VIEW


/*子线程运行的函数*/
void func(void *arg) {
    LOGD("child thread  arg = ", (char *) arg);
}


void test_thread() {
    /*创建线程对象。传入执行函数、参数及线程名称。*/
    rtc::PlatformThread th(func, (void *) "hello world", "child thread");

    /*创建子线程并运行*/
    th.Start();

    /*回收子线程*/
    th.Stop();

    LOGD("main thread");
}