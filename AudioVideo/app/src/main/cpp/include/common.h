//
// Created by Administrator on 2020/12/5 0005.
//

#ifndef AUDIOVIDEO_COMMON_H
#define AUDIOVIDEO_COMMON_H

#include <sys/time.h>

typedef unsigned char byte;

static inline long long getCurrentTime(){
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

static inline long getCurrentTimeSecSinceReferenceDate(){

    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec;
}




#endif //AUDIOVIDEO_COMMON_H
