//
// Created by yanghui4 on 2021/4/30.
//

#ifndef DHDEMO_RENDER_H
#define DHDEMO_RENDER_H

#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include "utils.h"


#define GET_STR(x) #x
static const char* vertexShader = GET_STR(

        uniform mat4 uMVPMatrix;
        uniform mat4 uSTMatrix;
        attribute vec4 aPosition;
        attribute vec4 aTextureCoord;
        varying vec2 vTextureCoord;

        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vTextureCoord = (uSTMatrix * aTextureCoord).xy;
        }
);


static const char* fragmentShader =
"#extension GL_OES_EGL_image_external : require\n"
"precision mediump float;\n"
"varying vec2 vTextureCoord;\n"
"uniform samplerExternalOES sTexture;\n"
"void main() {\n"
"    gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
"}\n";


static float mTriangleXYZData[] = {
        -1.0f, -1.0f, 0,
        1.0f, -1.0f, 0,
        -1.0f,  1.0f, 0,
        1.0f,  1.0f, 0,
};

static float mTriangleUVData[] = {
        0.f, 0.f,
        1.f,0.f,
        0.f,1.f,
        1.f,1.f,
};

void checkGlError(const char* op);

class KeyBoardRender{

public:
    KeyBoardRender(ANativeWindow* nativeWindow, int width, int height);
    ~KeyBoardRender();
    int getTexture();
    void drawFrame(int invert);
    float* getSTMatrix();
    void setSTMatrix(float* matrix);
    bool makeCurrent();
    void swap();
    bool capture = false;
    void snapCapture();
    unsigned char* getAddr(){
        return buf;
    }
    int getWidth(){
        return width;
    }
    int getHeight(){
        return height;
    }

private :
    bool eglSetup();
    bool setUp();
    GLint initShader(const char*source, GLint type);

    EGLDisplay eglDisplay;
    EGLSurface eglSurface;
    EGLContext eglContext;
    ANativeWindow* nativeWindow;
    float *mMVPMatrix;
    float *mSTMatrix;
    int program;
    int maPositionHandle;
    int maTextureHandle;
    int muMVPMatrixHandle;
    int muSTMatrixHandle;
    int textureId;
    int width;  //显示窗口宽
    int height; //显示窗口高
    unsigned char* buf; //显示图像buffer
};

#endif //DHDEMO_RENDER_H
