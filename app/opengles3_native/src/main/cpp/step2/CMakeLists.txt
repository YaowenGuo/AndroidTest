add_library(triangle triangle.cpp)
target_include_directories(triangle INTERFACE
        ${CMAKE_CURRENT_SOURCE_DIR}
        "../base/esUtil.h"
        )