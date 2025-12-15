cmake_minimum_required(VERSION 4.1.2)
set(CMAKE_CXX_STANDARD 20)

project("rtc_demo")
add_definitions("-DWEBRTC_POSIX")
add_definitions("-DABSL_OPTION_USE_STD_STRING_VIEW=0")

add_library(
        native_app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c
)
# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")

file(GLOB_RECURSE RTC_DEMO_SOURCES ${CMAKE_CURRENT_SOURCE_DIR} *.cpp)

add_library(rtc_demo SHARED ${RTC_DEMO_SOURCES})

add_library(webrtc STATIC IMPORTED)
set_target_properties(
        webrtc PROPERTIES
        IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libwebrtc.a
)

# 导入头文件仅能是目录，而不能直接指定 .h 文件。
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${ANDROID_NDK}/sources/android/native_app_glue
        "../../../../../lib/webrtc"
        "../../../../../lib/webrtc/third_party/jsoncpp/source/include"
        "../../../../../lib/webrtc/third_party/abseil-cpp"
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library(log-lib log)
find_library(camera-lib camera2ndk)
find_library(media-lib mediandk)


# Process Abseil's CMake build system
#add_subdirectory(../../../../../lib/webrtc/third_party/abseil-cpp
#        abseil-cpp
#        EXCLUDE_FROM_ALL)


target_link_libraries(
        rtc_demo
        ${OPENGL_LIB}
        android
        native_app_glue
        "-Wl,--whole-archive"
        webrtc
        "-Wl,--no-whole-archive"
        EGL
        GLESv3
        OpenSLES
        ${camera-lib}
        ${media-lib}
        ${log-lib}
)