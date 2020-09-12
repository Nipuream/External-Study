#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <android/bitmap.h>
#include "include/base.h"

ANativeWindow* nativeWindow;

extern "C" JNIEXPORT jstring JNICALL
Java_com_nipuream_audiovideo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_MainActivity_showBitmap(
        JNIEnv* env,
        jobject instace
){

    jclass  clazz = env->FindClass("com/nipuream/audiovideo/MainActivity");
    jfieldID  bitmapJava = env->GetFieldID(clazz,"bitmap", "Landroid/graphics/Bitmap;");
    jobject  bitmapNative = env->GetObjectField(instace, bitmapJava);

    //Get image info
    AndroidBitmapInfo info;
    if(AndroidBitmap_getInfo(env, bitmapNative, &info) != 0){
        LOGE("AndroidBitmap_getInfo failed ...");
        return ;
    }

    //Check image.
    if(info.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
        LOGE("Bitmap format is not RGBA_8888");
        return ;
    }

    //Get image addr.
    void* addr;
    int getRes = AndroidBitmap_lockPixels(env, bitmapNative, &addr);
    if(getRes != 0){
        LOGE("AndroidBitmap_lockPixels() failed !");
        return ;
    }

    int h = info.height;
    int w = info.width;

    LOGI("look bitmap information h : %d , w : %d", h, w);
    //process bitmap.

    ANativeWindow_setBuffersGeometry(nativeWindow, w, h, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer buffer;
    if(ANativeWindow_lock(nativeWindow, &buffer, 0)){
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
        return ;
    }

    LOGI("bufferwidth : %d, bufferStride : %d", buffer.width, buffer.stride);
    if(buffer.width >= buffer.stride){
        memcpy(buffer.bits, addr, h * w * 4);
    } else {
        //4字节对齐
        auto dst_bits = static_cast<uint8_t *>(buffer.bits);
        auto source_bits = static_cast<uint8_t *>(addr);

        for(int i = 0; i < h; i++){
            memcpy(dst_bits + buffer.stride * i * 4, source_bits + w * i * 4, w * 4);
        }
    }


    ANativeWindow_unlockAndPost(nativeWindow);
    //unlock everythings.
    AndroidBitmap_unlockPixels(env, bitmapNative);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_MainActivity_setSurface(
        JNIEnv* env,
        jobject instance,
        jobject surface
){

    LOGI("Get surface...");

    if(nativeWindow){
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
    }

    nativeWindow = ANativeWindow_fromSurface(env, surface);
}
