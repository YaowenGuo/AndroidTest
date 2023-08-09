cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 20)
project("test_jni")

file(GLOB_RECURSE SOURCE_FILE ${CMAKE_CURRENT_SOURCE_DIR} *.cpp *.c)
add_library(test_jni SHARED ${SOURCE_FILE})

find_library(log-lib log)

# For asan and ubsan, we need to copy some files from the NDK into our APK.
#get_filename_component(HINT_PATH ${ANDROID_C_COMPILER} DIRECTORY)
#set(ARCH_STR ${CMAKE_ANDROID_ARCH})
#if ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "arm64-v8a")
#    set(ARCH_STR "aarch64")
#elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "armeabi")
#    set(ARCH_STR "arm")
#elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "armeabi-v7a")
#    set(ARCH_STR "arm")
#elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "x86")
#    set(ARCH_STR "i686")
#elseif ("${CMAKE_ANDROID_ARCH_ABI}" STREQUAL "x86_64")
#    set(ARCH_STR "x86_64")
#endif()

# Use UBAsan
# Grab libclang_rt.ubsan_standalone-${ARCH_STR}-android.so from the NDK.
#file(GLOB UBSAN_GLOB "${HINT_PATH}/../lib64/clang/*/lib/linux")
#find_file(UBSAN
#        NAMES libclang_rt.ubsan_standalone-${ARCH_STR}-android.so
#        PATHS ${UBSAN_GLOB})
#get_filename_component(UBSAN_NAME ${UBSAN} NAME)
#set(UBSAN_NAME ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/${UBSAN_NAME})
#message("CMAKE_ARCHIVE_OUTPUT_DIRECTORY")
#message(${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
#add_custom_command(
#        TARGET test_jni PRE_BUILD
#        COMMAND ${CMAKE_COMMAND} -E copy ${UBSAN} ${UBSAN_NAME})
# Use HWASan

#file(REMOVE ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libclang_rt.asan-${ARCH_STR}-android.so
#        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/wrap.sh)

target_compile_options(test_jni PUBLIC -fsanitize=hwaddress -fno-omit-frame-pointer -fsanitize=undefined -fno-sanitize-recover=undefined -fno-sanitize=vptr)
target_link_options(test_jni PUBLIC -fsanitize=hwaddress -fsanitize=undefined -fno-sanitize-recover=undefined -fno-sanitize=vptr)


target_link_libraries(
        test_jni
        ${log-lib}
)