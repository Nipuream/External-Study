//
// Created by Administrator on 2020/9/12 0012.
//

#ifndef AUDIOVIDEO_BASE_H
#define AUDIOVIDEO_BASE_H

#include <android/log.h>
#include <android/native_window_jni.h>
#include <android/bitmap.h>
#include <string>
#include <pthread.h>

#define TAG "AudioVideo"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG,__VA_ARGS__)



void loadImage(void* addr, AndroidBitmapInfo& info);
void drawWithOpenGl(const char* path);
void* readYUVWithOpenGl(void *pVoid);


#endif //AUDIOVIDEO_BASE_H
