//
// Created by Albert on 3/9/21.
//

#ifndef ANDROIDTEST_VERTEX_SHADER_H
#define ANDROIDTEST_VERTEX_SHADER_H

#version 300 es  // 着色语言的版本，必须出现在着色器的第一行。300 es 表示 OpenGL ES 3.00
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() {
    gl_Position  = vPosition;
    gl_PointSize = 10.0;
    vColor = aColor;
}

#endif //ANDROIDTEST_VERTEX_SHADER_H
