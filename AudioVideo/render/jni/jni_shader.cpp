//
// Created by yanghui4 on 2021/4/30.
//

#include "utils.h"
#include "render.h"
#include <android/native_window_jni.h>
#include <android/bitmap.h>
#include <pthread.h>

static void capture_serialize(JNIEnv* env, jobject thiz);
bool DEBUG = false;

JavaVM *sVm = NULL;

extern "C"
JNIEXPORT
jint JNI_OnLoad(JavaVM *vm, void *res){

    LOGI("jni onLoad ...");
    sVm = vm;
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_dahua_render_KeyBoardRender_native_1init(JNIEnv *env, jobject obj, jobject output,
                                                  jint width, jint height) {

    LOGI("jni native init...");
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, output);

    if(nativeWindow == NULL){
        LOGI("create native window failed.");
        return JNI_FALSE;
    }

    auto *render = new KeyBoardRender(nativeWindow, width, height);
    jobject sobj = env->NewGlobalRef(obj);

    frame_copier *copier = new frame_copier(render->getShareContext());
    copier->obj = sobj;
    render->setCaptureCopier(copier);

    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "renderHandle", "J");
    env->SetLongField(obj, fid, (jlong)render);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_dahua_render_KeyBoardRender_GetOfflineTexture(JNIEnv *env, jobject thiz) {

    jclass cls = env->GetObjectClass(thiz);
    jfieldID  fid = env->GetFieldID(cls, "renderHandle", "J");

    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid));

    if(render == NULL){
        if(DEBUG)   LOGI("render is null.");
        return JNI_FALSE;
    }

    return render->getTexture();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_drawImage(JNIEnv *env, jobject thiz, jboolean invert) {


    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    render->makeCurrent();
    render->drawFrame(false);
    render->swap();

    if(render->capture){
        pthread_mutex_lock(&render->getFrameCopier()->preview_lock);

        //set frame copier texture.
        capture_serialize(env, thiz);
        pthread_mutex_lock(&render->getFrameCopier()->mLock);
        pthread_cond_signal(&render->getFrameCopier()->mCondition);
        pthread_mutex_unlock(&render->getFrameCopier()->mLock);

        pthread_cond_wait(&render->getFrameCopier()->preview_condition, &render->getFrameCopier()->preview_lock);
        render->capture = false;
        pthread_mutex_unlock(&render->getFrameCopier()->preview_lock);
    }
}

//更新纹理矩阵
extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_updateMatrix(JNIEnv *env, jobject thiz,
                                                  jfloatArray st_matrix) {

    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    jfloat * featureData = env->GetFloatArrayElements(st_matrix, JNI_FALSE);
    render->setSTMatrix(featureData);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_native_1release(JNIEnv *env, jobject thiz) {
    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    delete render;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_open_1debug(JNIEnv *env, jobject thiz,
                                                 jboolean debug) {
    DEBUG = debug;
}

void capture_serialize(JNIEnv* env, jobject thiz){
    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    //copy texture pixels.
    render->snapCapture();

    /*
    jclass clazz = env->GetObjectClass(thiz);
    jmethodID copyPixels = env->GetMethodID(clazz, "copyPixelsFromNative", "([B)V");

    jbyte *by = (jbyte*)render->getAddr();

    int size = render->capture_width * render->capture_height * 4;
    jbyteArray jbyteArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jbyteArray, 0, size, by);

    env->CallVoidMethod(thiz, copyPixels, jbyteArray);
    if(DEBUG) LOGI("copy pixels to java.");
    */
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_capture(JNIEnv *env, jobject thiz, jint width, jint height) {

    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }
    render->capture = true;
    render->capture_width = width;
    render->capture_height = height;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_render_KeyBoardRender_changeViewPort(JNIEnv *env, jobject thiz,
                                                    jint width, jint height) {
    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    LOGI("change view port");
    render->changeViewPort(width, height);
}