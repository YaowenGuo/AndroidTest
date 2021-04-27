add_library(triangle STATIC triangle.cpp)
target_include_directories(triangle INTERFACE
        ${CMAKE_CURRENT_SOURCE_DIR}
        "../base/esUtil.h"
        )