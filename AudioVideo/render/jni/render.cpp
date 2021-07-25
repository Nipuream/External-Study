//
// Created by yanghui4 on 2021/4/30.
//

#include <android/native_window.h>
#include <android/bitmap.h>
#include "render.h"
#include <unistd.h>
#include <sys/time.h>

extern bool DEBUG;

//void checkGlError(const char* op) {
//
//    int error;
//    if((error == glGetError()) != GL_NO_ERROR){
//        if(DEBUG) LOGI("op : %s, glError %d", op, error);
//    }
//}

KeyBoardRender::KeyBoardRender(ANativeWindow *nativeWindow, int width, int height) {

    //matrix init.
    this->mSTMatrix = new float[16];
    this->mMVPMatrix = new float[16];
    memset(this->mSTMatrix,0,sizeof(float)*16);
    memset(this->mMVPMatrix,0, sizeof(float)*16);

    buf = new unsigned char[width * height * 4];
//    buf = new unsigned char[CAPTURE_WIDTH * CAPTURE_HEIGHT * 4];
    this->width = width;
    this->height = height;

    this->nativeWindow = nativeWindow;
    if(!eglSetup()){
        if(DEBUG) LOGI("egl setup failed.");
        return ;
    }

    if(!makeCurrent()){
        if(DEBUG) LOGI("make current failed.");
        return;
    }

    if(!setUp()){
        if(DEBUG) LOGI("set up failed.");
    }
}

KeyBoardRender::~KeyBoardRender() {



    delete mSTMatrix;
    delete mMVPMatrix;
    delete buf;

    //销毁显示设备
    eglDestroySurface(eglDisplay,eglSurface);
    //销毁上下文
    eglDestroyContext(eglDisplay,eglContext);
    //释放窗口
    ANativeWindow_release(nativeWindow);
    //释放线程
    eglReleaseThread();
    //停止
    eglTerminate(eglDisplay);
    eglMakeCurrent(eglDisplay, nativeWindow, EGL_NO_SURFACE, eglContext);

    eglContext = EGL_NO_CONTEXT;
    eglSurface = EGL_NO_SURFACE;
    eglDisplay = EGL_NO_DISPLAY;
    nativeWindow = NULL;

    if(copier != NULL){
        delete copier;
        copier = NULL;
    }
}

bool
KeyBoardRender::eglSetup() {

    if(nativeWindow == NULL){
        if(DEBUG) LOGI("native window invaild.");
        return false;
    }

    eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(eglDisplay == EGL_NO_DISPLAY){
        if(DEBUG) LOGI("egl display failed.");
        return false;
    }
    if(DEBUG) LOGI("eglDisplay get successful");

    if(EGL_TRUE != eglInitialize(eglDisplay, 0, 0)){
        if(DEBUG) LOGI("egl initialize failed.");
        eglDisplay = NULL;
        return false;
    }
    if(DEBUG) LOGI("egl init successful");

    EGLConfig eglConfig;
    EGLint configNum;
    EGLint configSpec[] = {
            EGL_ALPHA_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
            EGL_NONE
    };

    if(EGL_TRUE != eglChooseConfig(eglDisplay, configSpec, &eglConfig, 1, &configNum)){
        if(DEBUG) LOGI("eglChooseConfig failed.");
        return false;
    }
    if(DEBUG) LOGI("egl choose config successful.");

    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };
    eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, ctxAttr);
    if(eglContext == EGL_NO_CONTEXT){
        if(DEBUG) LOGI("egl create context failed.");
        return false;
    }
    if(DEBUG) LOGI("egl create context successful.");

    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, nativeWindow, 0);
    if(eglSurface == EGL_NO_SURFACE){
        if(DEBUG) LOGI("egl create window surface failed.");
        return false;
    }
    if(DEBUG) LOGI("egl create window surface.");

    if(EGL_TRUE != eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)){
        if(DEBUG) LOGI("egl make current failed.");
        return false;
    }
    if(DEBUG) LOGI("egl make current sucessful.");

    return true;
}

void KeyBoardRender::changeViewPort(int width, int height) {
    LOGI("change view port : width : %d, height : %d",  width, height);
    this->width = width;
    this->height = height;

//    delete this->buf;
//    this->buf = new unsigned char[width * height * 4];
}

bool
KeyBoardRender::makeCurrent() {

    if(eglDisplay == NULL || eglSurface == NULL ||
       eglSurface == NULL || eglContext == NULL){
        if(DEBUG) LOGI("egl init failed.");
        return false;
    }

    if(EGL_TRUE != eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)){
        if(DEBUG) LOGI("egl make current failed.");
        return false;
    }
    if(DEBUG) LOGI("egl make current successful.");
    return true;
}

void
KeyBoardRender::swap() {

    if(eglDisplay == NULL || eglSurface == NULL){
        if(DEBUG) LOGI("egl init failed.");
        return ;
    }
    eglSwapBuffers(eglDisplay, eglSurface);
}

void KeyBoardRender::snapCapture() {

    //test. set context, textureid to frame copier...
    /*
    struct timeval tv;
    gettimeofday(&tv, NULL);
    LOGI("millisecond:%ld\n", tv.tv_sec * 1000 + tv.tv_usec/ 1000);

    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glActiveTexture(GL_TEXTURE0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, textureId, 0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, outputTxtId);
    glCopyTexSubImage2D(GL_TEXTURE_EXTERNAL_OES, 0, 0, 0,0, 0,capture_width, capture_height);
//    glCopyTexImage2D(GL_TEXTURE_EXTERNAL_OES, 0, GL_RGBA,0,0,capture_width, capture_height,0);
    checkGlError("glCopyTexSubImage2D");
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
    glDeleteFramebuffers(1, &fbo);

    gettimeofday(&tv, NULL);
    LOGI("millisecond:%ld\n", tv.tv_sec * 1000 + tv.tv_usec/ 1000);
    copier->setTexture(outputTxtId, capture_width, capture_height);
    */

    copier->setTexture(textureId, capture_width, capture_height);


    //test2.
//    memset(buf,0,width * height * 4);
//    glReadPixels(0,0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buf);

//===============================================================================

/*
    if(buf){
        if(DEBUG) LOGI("buf exits, delete buf.");
        delete buf;
    }

    if(DEBUG) LOGI("capture_width : %d, capture height : %d", capture_width, capture_height);
    buf = new unsigned char[capture_width * capture_height * 4];
    memset(buf, 0, capture_width * capture_height * 4);

    glActiveTexture(GL_TEXTURE0);
    checkGlError("glActiveTexture");
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
    checkGlError("bindTexture");

    GLuint FBO;
    glGenFramebuffers(1, &FBO);
    checkGlError("glGenFrameBuffers");

    glBindFramebuffer(GL_FRAMEBUFFER, FBO);
    checkGlError("bind framebuffer");

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,GL_TEXTURE_EXTERNAL_OES, textureId, 0);
    checkGlError("glFramebufferTexture2D");

    int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if(status == GL_FRAMEBUFFER_COMPLETE){
        glReadPixels(0,0,(GLsizei)capture_width, (GLsizei)capture_height, GL_RGBA, GL_UNSIGNED_BYTE, (GLvoid *)buf);
        checkGlError("glReadPixels");
    }

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, 0,0);
    checkGlError("glFrameBufferTexture2D");

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    checkGlError("glBindFrameBuffer");

    glDeleteFramebuffers(1, &FBO);
    checkGlError("glDeleteFrameBuffers");
*/
}

float*
KeyBoardRender::getSTMatrix(){
    return this->mSTMatrix;
}

void
KeyBoardRender::setSTMatrix(float *matrix) {
    this->mSTMatrix = matrix;
}

bool
KeyBoardRender::setUp() {

    matrixSetIdentityM(mSTMatrix);

    //==============  create program ================================

    GLint vsh = initShader(vertexShader, GL_VERTEX_SHADER);
    GLint fsh = initShader(fragmentShader, GL_FRAGMENT_SHADER);

    program = glCreateProgram();
    if(program == 0){
        if(DEBUG) LOGI("gl create program failed.");
        return false;
    }
    if(DEBUG) LOGI("gl create program successful.");

    glAttachShader(program, vsh);
    checkGlError("glAttach shader");
    glAttachShader(program, fsh);
    checkGlError("glAttach shader");

    glLinkProgram(program);
    GLint status = GL_FALSE;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if(status == GL_FALSE){
        if(DEBUG) LOGI("glLinkProgram failed.");
        glDeleteProgram(program);
        program = 0;
        return false;
    }

    //====================== get attr / bind texture. =================================

    maPositionHandle = glGetAttribLocation(program, "aPosition");
    if(maPositionHandle < 0){
        if(DEBUG) LOGI("get attr aPosition failed.");
        return false;
    }

    maTextureHandle = glGetAttribLocation(program, "aTextureCoord");
    if(maTextureHandle < 0){
        if(DEBUG) LOGI("get attr aTextureCoord failed.");
        return false;
    }

    muMVPMatrixHandle = glGetUniformLocation(program, "uMVPMatrix");
    if(muMVPMatrixHandle < 0){
        if(DEBUG) LOGI("get uMVPMatrix failed.");
        return false;
    }

    muSTMatrixHandle = glGetUniformLocation(program, "uSTMatrix");
    if(muSTMatrixHandle < 0){
        if(DEBUG) LOGI("get uSTmatrix failed.");
        return false;
    }

    //创建纹理
    GLuint textures[1] = {0};
    glGenTextures(1,textures);
    textureId = textures[0];
    if(DEBUG) LOGI("gen texture successful. texture : %d", textureId);

    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
    checkGlError("glBindTexture textureId");
//    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //线性过滤，使用距离当前渲染像素中心最近的4个纹素加权平均值
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // S 方向的贴图模式，将纹理坐标限制在 0.0 1.0的范围之内，如果超出了，会边缘拉伸填充处理
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    checkGlError("glTexParameter.");
    return true;
}

int
KeyBoardRender::getTexture(){
    return textureId;
}

GLint
KeyBoardRender::initShader(const char *source, GLint type) {
    GLint sh = glCreateShader(type);
    if(sh == 0){
        if(DEBUG) LOGI("glCreate shader %d failed", type);
        return 0;
    }

    glShaderSource(sh, 1, &source, 0); //代码长度 读到0 就表示字符串结尾
    glCompileShader(sh);

    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if(status == 0){
        if(DEBUG) LOGI("glCompileShader %d failed", type);
        if(DEBUG) LOGI("source %s", source);
        return 0;
    }

    if(DEBUG) LOGI("glCompileShader %d success", type);
    return sh;
}

void
KeyBoardRender::drawFrame(int invert) {

    if(DEBUG) LOGI("glViwPort : width : %d, height : %d", width, height);
    glViewport(0,0, width, height);
    checkGlError("onDraw frame start.");

    //获取转换矩阵, 已由JNI层完成, Done.

    if(invert){
        mSTMatrix[5] = -mSTMatrix[5];
        mSTMatrix[13] = 1.0f - mSTMatrix[13];
    }

    glClearColor(0.0f, 0.0f, 0.0f,1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(program);
    checkGlError("glUseProgram");
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

    //顶点坐标
    glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 12, mTriangleXYZData);
    checkGlError("glVertexAttribPointer maPosition");
    glEnableVertexAttribArray(maPositionHandle);
    checkGlError("glEnableVertexAttribArray maPositionHandle");

    //纹理坐标
    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, false, 8,mTriangleUVData);
    checkGlError("glVertexAttribPointer maTextureHandle");
    glEnableVertexAttribArray(maTextureHandle);
    checkGlError("glEnableVertexAttribArray maTextureHandle");

    matrixSetIdentityM(mMVPMatrix);

    //将矩阵传入着色器
    glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix);
    glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix);

    //绘制
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    checkGlError("glDrawArrays");

    //解绑
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
}
