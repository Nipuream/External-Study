//
// Created by yanghui4 on 2021/4/30.
//

#ifndef DHDEMO_UTILS_H
#define DHDEMO_UTILS_H

#include <string.h>
#include <android/log.h>
#include <jni.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "kb_render::", __VA_ARGS__))


void matrixSetIdentityM(float *m);


#endif //DHDEMO_UTILS_H
