//
// Created by Administrator on 2020/9/12 0012.
//

#ifndef AUDIOVIDEO_BASE_H
#define AUDIOVIDEO_BASE_H

#include <android/log.h>

#define TAG "AudioVideo"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG,__VA_ARGS__)

#endif //AUDIOVIDEO_BASE_H
