#include <jni.h>
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
Java_com_nipuream_audiovideo_MainActivity_playYUV(
        JNIEnv* env,
        jobject instance,
        jstring jstr){

    const char* path = env->GetStringUTFChars(jstr, 0);
    pthread_t  pid;
    pthread_create(&pid, NULL, readYUVWithOpenGl, (void *)path);
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

    loadImage(addr, info);
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

void* readYUVWithOpenGl(void *pVoid){
    const char* path = reinterpret_cast<const char*>(pVoid);
    drawWithOpenGl(path);
    return 0;
}
