cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 14)

project("rtc_demo")
add_definitions("-DWEBRTC_POSIX")


add_library(
        native_app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c
)
# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")

file(GLOB_RECURSE RTC_DEMO_SOURCES ${CMAKE_CURRENT_SOURCE_DIR} *.cpp)
#file(
#        GLOB allCpp
#        base/*.cpp
#        camera/*.cpp
#        utils/*.cpp
#        main/*.cpp
#        peer/*.cpp
#        testthread/
#)

add_library(rtc_demo SHARED ${RTC_DEMO_SOURCES})

add_library(webrtc STATIC IMPORTED)
set_target_properties(
        webrtc PROPERTIES
        IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libwebrtc.a
)
# not working for static library.
#SET_TARGET_PROPERTIES(
#        webrtc PROPERTIES
#        COMPILE_FLAGS "-fdebug-prefix-map=../..=${CMAKE_CURRENT_SOURCE_DIR}/../../../../../lib/webrtc"
#)


# 导入头文件仅能是目录，而不能直接指定 .h 文件。
#target_include_directories(
#        rtc_demo PUBLIC
#        ${ANDROID_NDK}/sources/android/native_app_glue
#        ${CMAKE_CURRENT_SOURCE_DIR}
#)

include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(
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


#add_subdirectory(../../../../../lib/socket.io-client-cpp
#        sioclient
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
        #        sioclient
        ${camera-lib}
        ${media-lib}
        ${log-lib}
)