cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 20)
project("test_jni")

file(GLOB_RECURSE SOURCE_FILE ${CMAKE_CURRENT_SOURCE_DIR} *.cpp *.c)
add_library(test_jni SHARED ${SOURCE_FILE})

find_library(log-lib log)
# Use HWASan
target_compile_options(test_jni PUBLIC -fsanitize=hwaddress -fno-omit-frame-pointer -fsanitize=undefined -fno-sanitize-recover=undefined)
target_link_options(test_jni PUBLIC -fsanitize=hwaddress -fsanitize=undefined -fno-sanitize-recover=undefined)

# For asan and ubsan, we need to copy some files from the NDK into our APK.
get_filename_component(HINT_PATH ${ANDROID_C_COMPILER} DIRECTORY)
set(ARCH_STR ${CMAKE_ANDROID_ARCH})
if ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "arm64-v8a")
    set(ARCH_STR "aarch64")
elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "armeabi")
    set(ARCH_STR "arm")
elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "armeabi-v7a")
    set(ARCH_STR "arm")
elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "x86")
    set(ARCH_STR "i686")
elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "x86_64")
    set(ARCH_STR "x86_64")
endif()

# Use UBAsan
# Grab libclang_rt.ubsan_standalone-${ARCH_STR}-android.so from the NDK.
file(GLOB UBSAN_GLOB "${HINT_PATH}/../lib64/clang/*/lib/linux")
find_file(UBSAN
        NAMES libclang_rt.ubsan_standalone-${ARCH_STR}-android.so
        PATHS ${UBSAN_GLOB})
get_filename_component(UBSAN_NAME ${UBSAN} NAME)
set(UBSAN_NAME ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}/${UBSAN_NAME})
add_custom_command(
        TARGET test_jni PRE_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy ${UBSAN} ${UBSAN_NAME})

target_link_libraries(
        test_jni
        ${log-lib}
)