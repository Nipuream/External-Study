//
// Created by 246747 on 2021/5/22.
//

#include "render.h"
#include <pthread.h>
#include <unistd.h>
#include "utils.h"

extern bool DEBUG;
extern JavaVM *sVm;

frame_copier::frame_copier(EGLContext context) {

    this->eglContext = context;
    exit = false;
    pthread_mutex_init(&mLock, NULL);
    pthread_mutex_init(&preview_lock, NULL);
    pthread_cond_init(&mCondition, NULL);
    pthread_cond_init(&preview_condition, NULL);

    if(pthread_attr_init(&attr) != 0){
        LOGI("frame_copier pthread init error.");
        return ;
    }

    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    pthread_create(&mthread, &attr, copyPixels, this);
}

frame_copier::~frame_copier() {

    if(buf){
        delete [] buf;
        buf = NULL;
    }

    pthread_attr_destroy(&attr);
    pthread_cond_destroy(&mCondition);
    pthread_cond_destroy(&preview_condition);
    pthread_mutex_destroy(&mLock);
    pthread_mutex_destroy(&preview_lock);
    exit = true;

    JNIEnv *env;
    if(sVm->AttachCurrentThread(&env, NULL) != JNI_OK){
        LOGI("frame_copier attach vm failed.");
        return ;
    }

    env->DeleteGlobalRef(obj);
}

void frame_copier::setTexture(int texture, int width, int height) {

    this->inputTexture = texture;
    this->width = width;
    this->height = height;

    //init output txtId.
    glGenTextures(1, getOutputTexture());
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *getOutputTexture());
    checkGlError("glBindTexture textureId");
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //线性过滤，使用距离当前渲染像素中心最近的4个纹素加权平均值
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // S 方向的贴图模式，将纹理坐标限制在 0.0 1.0的范围之内，如果超出了，会边缘拉伸填充处理
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    LOGI("<-----  frame_copier notify copy ----->  ");
}

void makeCurrent(frame_copier* copier){

    if(!eglMakeCurrent(copier->eglDisplay, copier->eglSurface,
                       copier->eglSurface, copier->eglContext)){
        if(DEBUG) LOGI("frame_copier egl make current failed.");
    }

    if(DEBUG) LOGI("frame_copier egl make current successful.");
}

bool initOpengl(frame_copier *copier){

    //初始化 Opengl 环境
    copier->eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(copier->eglDisplay == EGL_NO_DISPLAY){
        if(DEBUG) LOGI("frame_copier egl display failed.");
        return false;
    }
    if(DEBUG) LOGI("frame_copier eglDisplay get successful");

    if(EGL_TRUE != eglInitialize(copier->eglDisplay, 0, 0)){
        if(DEBUG) LOGI("frame_copier egl initialize failed.");
        copier->eglDisplay = NULL;
        return false;
    }
    if(DEBUG) LOGI("frame_copier egl init successful");

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

    if(EGL_TRUE != eglChooseConfig(copier->eglDisplay, configSpec, &eglConfig, 1, &configNum)){
        if(DEBUG) LOGI("frame_copier eglChooseConfig failed.");
        return false;
    }
    if(DEBUG) LOGI("frame_copier egl choose config successful.");

    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };
    copier->eglContext = eglCreateContext(copier->eglDisplay, eglConfig, copier->eglContext, ctxAttr);
    if(copier->eglContext == EGL_NO_CONTEXT){
        if(DEBUG) LOGI("frame_copier egl create context failed.");
        return false;
    }
    if(DEBUG) LOGI("frame_copier egl create context successful.");

    int attr_list [] = { EGL_WIDTH, 64,
                         EGL_HEIGHT, 64,
                         EGL_NONE};

    copier->eglSurface = eglCreatePbufferSurface(copier->eglDisplay, eglConfig, attr_list);
    if(copier->eglSurface == EGL_NO_SURFACE){
        if(DEBUG) LOGI("frame_copier egl create pbuffer surface failed.");
        return false;
    }

    if(DEBUG) LOGI("frame_copier egl create pbuffer surface successful.");

    //非必须执行
    makeCurrent(copier);
//    if(!eglMakeCurrent(copier->eglDisplay, copier->eglSurface, copier->eglSurface, copier->eglContext)){
//        if(DEBUG) LOGI("egl make current failed.");
//        return false;
//    }

    //init output texture.
    glGenTextures(1, copier->getOutputTexture());
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *copier->getOutputTexture());
    checkGlError("glBindTexture textureId");
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //线性过滤，使用距离当前渲染像素中心最近的4个纹素加权平均值
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // S 方向的贴图模式，将纹理坐标限制在 0.0 1.0的范围之内，如果超出了，会边缘拉伸填充处理
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    copier->mGLProgId = loadProgram(vertexShader, fragmentShader);
    if(!copier->mGLProgId){
        LOGI("Could not create program.");
    }

    copier->mGLVertexCoords = glGetAttribLocation(copier->mGLProgId, "aPosition");
    checkGlError("glGetAttribLocation vPosition");
    copier->mGLTextureCoords = glGetAttribLocation(copier->mGLProgId, "aTextureCoord");
    checkGlError("glGetAttribLocation vTexCords");
    copier->mGLUniformTexture = glGetUniformLocation(copier->mGLProgId, "sTexture");
    checkGlError("glGetAttribLocation yuvTexSampler");

    return true;
}

void releaseOpengl(frame_copier* copier){

    if(copier->eglDisplay == NULL
       || copier->eglSurface == NULL
       || copier->eglContext == NULL){
        return ;
    }

    //销毁显示设备
    eglDestroySurface(copier->eglDisplay,copier->eglSurface);
    //销毁上下文
    eglDestroyContext(copier->eglDisplay,copier->eglContext);
    //释放线程
    eglReleaseThread();
    //停止
    eglTerminate(copier->eglDisplay);
    eglMakeCurrent(copier->eglDisplay, copier->eglSurface, EGL_NO_SURFACE, copier->eglContext);

    copier->eglContext = EGL_NO_CONTEXT;
    copier->eglSurface = EGL_NO_SURFACE;
    copier->eglDisplay = EGL_NO_DISPLAY;

    glDeleteTextures(1, copier->getInputTexture());
    glDeleteTextures(1, copier->getOutputTexture());
    glDeleteProgram(copier->mGLProgId);
}

void fboCopy(frame_copier* copier){

    if(DEBUG) LOGI("frame_copier capture_width : %d, capture height : %d", copier->width, copier->height);
    copier->buf = new unsigned char[copier->width * copier->height * 4];
    memset(copier->buf, 0, copier->width * copier->height * 4);

    GLuint FBO;
    glGenFramebuffers(1, &FBO);
    checkGlError("frame_copier glGenFrameBuffers");

    glBindFramebuffer(GL_FRAMEBUFFER, FBO);
    checkGlError("frame_copier bind framebuffer");

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,GL_TEXTURE_EXTERNAL_OES, *copier->getInputTexture(), 0);
    checkGlError("frame_copier glFramebufferTexture2D");

    glActiveTexture(GL_TEXTURE0);
    checkGlError("frame_copier glActiveTexture");
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *copier->getInputTexture());
    checkGlError("frame_copier bindTexture");

    int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if(status == GL_FRAMEBUFFER_COMPLETE){
        glReadPixels(0,0,(GLsizei)copier->width, (GLsizei)copier->height, GL_RGBA, GL_UNSIGNED_BYTE, (GLvoid *)copier->buf);
        checkGlError("frame_copier glReadPixels");
    } else {
        if(DEBUG) LOGI("frame_copier framebuffer status : %d", status);
    }

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, 0,0);
    checkGlError("frame_copier glFrameBufferTexture2D");

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    checkGlError("frame_copier glBindFrameBuffer");

    glDeleteFramebuffers(1, &FBO);
    checkGlError("frame_copier glDeleteFrameBuffers");
}

void copyToJava(frame_copier* copier){

    JNIEnv *env;
    if(sVm->AttachCurrentThread(&env, NULL) != JNI_OK){
        LOGI("frame_copier attach vm failed.");
        return ;
    }

    jclass clazz = env->GetObjectClass(copier->obj);
    jmethodID copyPixels = env->GetMethodID(clazz, "copyPixelsFromNative", "([B)V");

//    jbyte *by = (jbyte*)render->getAddr();
//    int size = render->capture_width * render->capture_height * 4;

    jbyte *by = (jbyte*)copier->buf;
    int size = copier->width * copier->height * 4;

    jbyteArray jbyteArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jbyteArray, 0, size, by);

    env->CallVoidMethod(copier->obj, copyPixels, jbyteArray);
    if(DEBUG) LOGI("frame_copier copy pixels to java.");
}

void copyWithCoords(frame_copier* copier){

    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *copier->getOutputTexture());
    checkGlError("glBindTexture");

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, *copier->getOutputTexture(), 0);
    checkGlError("glFramebufferTexture2D");

    glUseProgram(copier->mGLProgId);

    glVertexAttribPointer(copier->mGLVertexCoords, 2, GL_FLOAT, GL_FALSE, 0 , mTriangleXYZData);
    glEnableVertexAttribArray(copier->mGLVertexCoords);
    glVertexAttribPointer(copier->mGLTextureCoords, 2, GL_FLOAT, GL_FALSE, 0, mTriangleUVData);
    glEnableVertexAttribArray(copier->mGLTextureCoords);

    //binding input texture.
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *copier->getInputTexture());
    glUniform1i(copier->mGLUniformTexture, 0);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableVertexAttribArray(copier->mGLVertexCoords);
    glDisableVertexAttribArray(copier->mGLTextureCoords);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, 0, 0);
    glDeleteFramebuffers(1, &fbo);
}

/**
 * 这种方案行不通，可能驱动不支持，FBO附着失败 导致拷贝失败
 * @param copier
 */
void copyTexture(frame_copier* copier){

    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    glActiveTexture(GL_TEXTURE0);
    //attach source texture to the fbo.
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, *copier->getInputTexture(), 0);
    //bind the destination texture.
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, *copier->getOutputTexture());

    int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if(status != GL_FRAMEBUFFER_COMPLETE){
        LOGI("frame_copier frame buffer status is not true.");
    }

    //copy
    glCopyTexSubImage2D(GL_TEXTURE_EXTERNAL_OES, 0,0,0,0,0, copier->width, copier->height);
    checkGlError("glCopyTexSubImage2D...");

    //unbind the fbo.
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_EXTERNAL_OES, 0, 0);
    glDeleteFramebuffers(1, &fbo);
}

void* copyPixels(void *addr) {

    LOGI("=======  frame_copier create copy pixels thread. ==========");

    auto copier = reinterpret_cast<frame_copier *>(addr);
    if(copier == NULL){
        LOGI("frame_copier thread %d exit run.", getpid());
        return NULL;
    }

    initOpengl(copier);

    while (!copier->exit){

        pthread_mutex_lock(&copier->mLock);
        LOGI("frame_copier wait render thread set Texture ...");
        pthread_cond_wait(&copier->mCondition, &copier->mLock);

        LOGI("frame_copier copy textureId %d pixels", copier->getInputTexture());
        if(copier->buf != NULL){
            delete [] copier->buf;
        }

        makeCurrent(copier);
        //1. copy preview texture.
//        copyTexture(copier);
        copyWithCoords(copier);

        pthread_mutex_lock(&copier->preview_lock);
        pthread_cond_signal(&copier->preview_condition);
        pthread_mutex_unlock(&copier->preview_lock);

        //2. bind frame buffer copy pixels.
        fboCopy(copier);

        //3. copy pixels to Java.
        copyToJava(copier);
        //unlock.
        pthread_mutex_unlock(&copier->mLock);
    }

    releaseOpengl(copier);
    LOGI("============== frame copier thread exit. ============");
}

