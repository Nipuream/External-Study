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


class frame_copier {

public:
    frame_copier(EGLContext context);
    ~frame_copier();

    EGLContext eglContext;
    EGLDisplay eglDisplay;
    EGLSurface eglSurface;
    bool exit;

    //显示图像buffer
    unsigned char* buf = NULL;
    //截图宽高
    int width, height;

    pthread_mutex_t mLock;
    pthread_mutex_t preview_lock;
    pthread_cond_t mCondition;
    pthread_cond_t preview_condition;

    void setTexture(int texture, int width, int height);
    GLuint*  getInputTexture(){
        return &inputTexture;
    }
    GLuint* getOutputTexture(){
        return &outputTexture;
    }
    GLuint mGLProgId;
    GLint mGLUniformTexture;
    GLuint mGLVertexCoords;
    GLuint mGLTextureCoords;

    jobject obj;
private:
    pthread_t  mthread;
    pthread_attr_t attr;
    GLuint inputTexture;
    GLuint outputTexture;
};

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
    //此帧是否需要截图
    bool capture = false;
    //截图宽高
    int capture_width ;
    int capture_height;
    void snapCapture();
    void changeViewPort(int width, int height);
    unsigned char* getAddr(){
        return buf;
    }
    int getWidth(){
        return width;
    }
    int getHeight(){
        return height;
    }

    void setCaptureCopier(frame_copier* copier){
        this->copier = copier;
    }
    frame_copier* getFrameCopier(){
        return copier;
    }
    EGLContext getShareContext(){
        return eglContext;
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
    //显示宽高
    int width;
    int height;
    //显示图像buffer
    unsigned char* buf;
    frame_copier* copier;
};



void checkGlError(const char* op);
void* copyPixels(void* addr);


#endif //DHDEMO_RENDER_H
