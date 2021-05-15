// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//
// ESUtil.c
//
//    A utility library for OpenGL ES.  This library provides a
//    basic common framework for the example applications in the
//    OpenGL ES 3.0 Programming Guide.
//

///
//  Includes
//
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include "esUtil.h"

#ifdef ANDROID

#include <android/log.h>
#include <android_native_app_glue.h>
#include <android/asset_manager.h>

typedef AAsset esFile;
#else
typedef FILE esFile;
#endif


///
//  Macros
//
#define INVERTED_BIT            (1 << 5)

///
//  Types
//

typedef struct {
    unsigned char IdSize,
            MapType,
            ImageType;
    unsigned short PaletteStart,
            PaletteSize;
    unsigned char PaletteEntryDepth;
    unsigned short X,
            Y,
            Width,
            Height;
    unsigned char ColorDepth,
            Descriptor;

} TGA_HEADER;


///
// Create a shader object, load the shader source, and
// compile the shader.
//
GLuint load_shader(GLenum type, const char *shaderSrc) {
    GLint compiled;

    // Create the shader object
    GLuint shader = glCreateShader(type);
    if (shader == 0) {
        return 0;
    }

    // Load the shader source
    glShaderSource(shader, 1, &shaderSrc, NULL);
    // Compile the shader
    glCompileShader(shader);

    // Check the compile status
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);

    if (!compiled) {
        GLint infoLen = 0;

        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);

        if (infoLen > 1) {
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);

            glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
            LOGE("Error compiling shader:\n%s\n", infoLog);

            free(infoLog);
        }

        glDeleteShader(shader);
        return 0;
    }

    return shader;
}

void localGetConfigAttrib(EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value) {
    if (!eglGetConfigAttrib(dpy, config, attribute, value)) {
        *value = -1;
    }
}

GLuint printEGLConfig(EGLDisplay display) {
    EGLint numConfigs;
    if (eglGetConfigs(display, nullptr, 0, &numConfigs)) {
        std::unique_ptr<EGLConfig[]> supportedConfigs(new EGLConfig[numConfigs]);
//        assert(supportedConfigs);
        eglGetConfigs(display, supportedConfigs.get(), numConfigs, &numConfigs);
        LOGE("OPENGL: config size: %d", numConfigs);

        LOGI("|| -------- | ----------- | -------- | ---------- | --------- | ---------- | ---------- |");
        LOGI("||  Config  | BUFFER_SIZE | RED_SIZE | GREEN_SIZE | BLUE_SIZE | DEPTH_SIZE | -----------|");
        auto i = 0;
        for (; i < numConfigs; i++) {
            auto &cfg = supportedConfigs[i];
            EGLint id;
            EGLint bufferSize;
            EGLint r, g, b, d;
            localGetConfigAttrib(display, cfg, EGL_CONFIG_ID, &id);
            localGetConfigAttrib(display, cfg, EGL_BUFFER_SIZE, &bufferSize);
            localGetConfigAttrib(display, cfg, EGL_RED_SIZE, &r);
            eglGetConfigAttrib(display, cfg, EGL_GREEN_SIZE, &g);
            eglGetConfigAttrib(display, cfg, EGL_BLUE_SIZE, &b);
            eglGetConfigAttrib(display, cfg, EGL_DEPTH_SIZE, &d);
            LOGI("|| %8d | %16d | %12d | %12d | %12d | %12d ", id, bufferSize, r, g, b, d);
        }
        LOGI("|| -------- | ----------- | -------- | ---------- | --------- | ---------- | ---------- |");
        return EGL_TRUE;
    } else {
        EGLint error = eglGetError();
        if (error == EGL_NOT_INITIALIZED) {
            LOGE("OPENGL: EGL_NOT_INITIALIZED");
        } else if (error == EGL_BAD_PARAMETER) {
            LOGE("OPENGL: EGL_NOT_INITIALIZED");
        } else {
            LOGE("OPENGL: get configs error.");
        }
        return GL_FALSE;
    }
}
