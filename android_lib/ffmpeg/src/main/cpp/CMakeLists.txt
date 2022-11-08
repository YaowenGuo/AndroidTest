# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


#打印当前 cmakeLists 的路径
#message(AUTHOR_WARNING "当前 CMakeLists 路径: ${CMAKE_SOURCE_DIR} \n cpu架构：${CMAKE_ANDROID_ARCH_ABI}")


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#找到包含所有的cpp文件
file(GLOB allCpp *.cpp)

add_library( # Sets the name of the library.
        ffmpeg_lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${allCpp})

add_library(
        avformat
        SHARED
        IMPORTED)

message(AUTHOR_WARNING "路径${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libavcodec.so" )
set_target_properties( # Specifies the target library.
        avformat

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so )

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        avcodec

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libavcodec.so )

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        avfilter

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libavfilter.so )

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        avutil

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libavutil.so )

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        swresample

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libswresample.so )

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        swscale

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libswscale.so )

add_library(postproc
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        postproc

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libpostproc.so )



#[[add_library(x264
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        x264

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libx264.so.161 )

add_library(x264_161
        SHARED
        IMPORTED)
set_target_properties( # Specifies the target library.
        x264_161

        # Specifies the parameter you want to define.
        PROPERTIES IMPORTED_LOCATION

        # Provides the path to the library you want to import.
        ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/libx264.so.161 )]]

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

# 引入FFmpeg的头文件
include_directories(${CMAKE_SOURCE_DIR}/include)



# 引入FFmpeg的库文件，设置内部的方式引入，指定库的目录是 -L  指定具体的库-l
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/${CMAKE_ANDROID_ARCH_ABI}")

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ffmpeg_lib

        # 具体的库文件，链接到总库
        # 这样写会报错，有坑
        # avcodec avfilter avformat avutil swresample swscale

        # 先把有依赖的库，先依赖进来
        avformat avcodec avfilter avutil swresample swscale postproc

        ${log-lib})