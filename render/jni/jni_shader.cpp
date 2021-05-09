//
// Created by yanghui4 on 2021/4/30.
//

#include "utils.h"
#include "render.h"
#include <android/native_window_jni.h>
#include <android/bitmap.h>

void capture_serialize(JNIEnv* env, jobject thiz);
bool DEBUG = false;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_native_1init(JNIEnv *env, jobject obj, jobject output,
                                                                  jint width, jint height) {

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, output);

    if(nativeWindow == NULL){
        LOGI("create native window failed.");
        return JNI_FALSE;
    }

    auto *render = new KeyBoardRender(nativeWindow, width, height);

    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "renderHandle", "J");
    env->SetLongField(obj, fid, (jlong)render);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_GetOfflineTexture(JNIEnv *env, jobject thiz) {

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
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_drawImage(JNIEnv *env, jobject thiz, jboolean invert) {


    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

//    jclass cls = env->GetObjectClass(thiz);
//    jfieldID  fid = env->GetFieldID(cls, "surfaceTexture", "Landroid/graphics/SurfaceTexture;");
//
//    jobject obj = env->GetObjectField(thiz, fid);
//    if(DEBUG) LOGI("obj address : 0x%x", obj);
//    jclass  objcls = env->GetObjectClass(obj);
//    jmethodID getMatrix = env->GetMethodID(objcls, "getTransformMatrix", "([F)V");
//    if(DEBUG) LOGI("getMatrix : 0x%x", getMatrix);
//
//    if(obj == NULL || getMatrix == NULL){
//        if(DEBUG) LOGI("drawImage failed.");
//        return;
//    }

    render->makeCurrent();
    render->drawFrame(false);
    if(render->capture){
        capture_serialize(env, thiz);
        render->capture = false;
    }
    render->swap();
}

//更新纹理矩阵
extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_updateMatrix(JNIEnv *env, jobject thiz,
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
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_native_1release(JNIEnv *env, jobject thiz) {
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
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_open_1debug(JNIEnv *env, jobject thiz,
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

    jclass clazz = env->GetObjectClass(thiz);
    jmethodID copyPixels = env->GetMethodID(clazz, "copyPixelsFromNative", "([B)V");

    jbyte *by = (jbyte*)render->getAddr();

    int size = render->getHeight() * render->getWidth() * 4;
    jbyteArray jbyteArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jbyteArray, 0, size, by);

    env->CallVoidMethod(thiz, copyPixels, jbyteArray);
    if(DEBUG) LOGI("copy pixels to java.");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dahua_dhcontrolcenter_render_KeyBoardRender_capture(JNIEnv *env, jobject thiz) {

    jclass cls1 = env->GetObjectClass(thiz);
    jfieldID  fid1 = env->GetFieldID(cls1, "renderHandle", "J");
    auto *render = reinterpret_cast<KeyBoardRender*>(env->GetLongField(thiz, fid1));

    if(render == NULL){
        if(DEBUG) LOGI("render is null..");
        return ;
    }

    render->capture = true;
}