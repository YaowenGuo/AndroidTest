# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 17)

project("rtc_demo")

##官方标准配置
#set(${CMAKE_C_FLAGS}, "${CMAKE_C_FLAGS}")

add_library(native_app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fpic")
# now build app's shared lib
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fpic  -Wall -v")

add_definitions("-DWEBRTC_POSIX")


# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.

# Gradle automatically packages shared libraries with your APK.

#找到包含所有的cpp文件
file(GLOB allCpp
        base/*.cpp
        main/*.cpp
        peer/*.cpp
        capture/*.cpp
        permissition/*.cpp
        )


add_library( # Sets the name of the library.
        rtc_demo

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${allCpp})


# 引入FFmpeg的头文件
# 导入头文件仅能是目录，而不能直接指定 .h 文件。
target_include_directories(rtc_demo PUBLIC
        ${ANDROID_NDK}/sources/android/native_app_glue
        ${CMAKE_SOURCE_DIR}/include
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)
# camera native api
find_library(camera-lib camera2ndk)
find_library(media-lib mediandk)

#
#add_library(webrtc-lib
#        STATIC
#        ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libwebrtc.a)
#set_target_properties( # Specifies the target library.
#        webrtc-lib
#
#        # Specifies the parameter you want to define.
#        PROPERTIES IMPORTED_LOCATION
#
#        # Provides the path to the library you want to import.
#        ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libwebrtc.a) # webrtc 编译

add_library(webrtc STATIC IMPORTED)
set_target_properties(webrtc PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libwebrtc.a)


include_directories(
        "../../../../../lib/webrtc"
)

# Process Abseil's CMake build system
add_subdirectory(../../../../../lib/webrtc/third_party/abseil-cpp
        abseil-cpp
        EXCLUDE_FROM_ALL)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries( # Specifies the target library.
        rtc_demo
        ${OPENGL_LIB}
        android
        native_app_glue
        webrtc
        EGL
        GLESv3
        OpenSLES
#        ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libjingle_peerconnection_so.so
        absl::flags
        ${camera-lib}
        ${media-lib}
        ${log-lib})