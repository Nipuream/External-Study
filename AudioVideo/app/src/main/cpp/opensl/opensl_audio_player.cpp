//
// Created by Administrator on 2021/1/30 0030.
//

#include <pthread.h>
#include <string.h>
#include "../include/opensl_audio_player.h"


#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"OpenSL",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"OpenSL",FORMAT,##__VA_ARGS__);

void playerCallback(SLAndroidSimpleBufferQueueItf bq, void* context);


OpenSLAudioPlay::OpenSLAudioPlay(int sampleRate, int sampleFormat, int channels)
:  mAudioEngine(new AudioEngine()),mPlayerObj(NULL), mPlayer(NULL),
 mBufferQueue(NULL), mEffectSend(NULL), mVolume(NULL),
 mSampleRate((SLmilliHertz) sampleRate * 1000),mSampleFormat(sampleFormat),
 mChannels(channels), mBufSize(0), mIndex(0)
{
     pthread_mutex_init(&mMutex, NULL);
     mBuffers[0] = NULL;
     mBuffers[1] = NULL;
}

OpenSLAudioPlay::~OpenSLAudioPlay() {

}

bool
OpenSLAudioPlay::init() {

     SLresult result;

     //第三步： 创建播放器
     //3.1 配置输入声音信息
     //创建buffer缓冲类型的 2个队列
     SLDataLocator_AndroidBufferQueue locBufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,2};
     //pcm数据格式
     SLDataFormat_PCM formatPcm = {SL_DATAFORMAT_PCM, (SLuint32)mChannels,mSampleRate,
                                   (SLuint32)mSampleFormat, (SLuint32)mSampleFormat,
                                   mChannels == 2 ? 0 : SL_SPEAKER_FRONT_CENTER,
                                   SL_BYTEORDER_LITTLEENDIAN};

     if(mSampleRate){
          formatPcm.samplesPerSec = mSampleRate;
     }

     //数据源，将上述配置信息放到这个数据源中
     SLDataSource audioSrc = {&locBufq, &formatPcm};


     //3.2 配置音轨（输出）
     //设置混音器
     SLDataLocator_OutputMix locOutpuMix = {SL_DATALOCATOR_OUTPUTMIX, mAudioEngine->outputMixObj};
     SLDataSink audioSink = {&locOutpuMix, NULL};


     //create audio player.
     //需要的接口 操作队列的接口
     const SLInterfaceID  ids[3]  = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_EFFECTSEND};
     const SLboolean  req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

     //3.3 创建播放器
     result = (*mAudioEngine->engine)->CreateAudioPlayer(mAudioEngine->engine, &mPlayerObj,
                                                        &audioSrc, &audioSink,
                                                        mSampleRate ? 2 : 3, ids, req);

     if(result != SL_RESULT_SUCCESS){
          LOGE("CreateAudioPlayer failed : %d", result);
          return false;
     }

     //3.4 初始化播放器
     result = (*mPlayerObj)->Realize(mPlayerObj, SL_BOOLEAN_FALSE);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj Realize failed : %d", result);
          return false;
     }

     //3.5获取播放器接口
     result = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_PLAY, &mPlayer);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj GetInterface mPlayer failed : %d", result);
          return false;
     }

     //第四步 ： 设置播放的回调函数
     //4.1 获取播放器队列的接口：
     result = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_BUFFERQUEUE, &mBufferQueue);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj GetInterface BufferQueue failed : %d", result);
          return false;
     }

     //4.2 设置回调
     result = (*mBufferQueue)->RegisterCallback(mBufferQueue, playerCallback, this);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj RegisterCall failed : %d", result);
          return false;
     }

     mEffectSend = NULL;
     if(mSampleRate == 0){
          result = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_EFFECTSEND, &mEffectSend);
          if(result != SL_RESULT_SUCCESS){
               LOGE("mPlayerObj GetInterface effectsend failed : %d", result);
               return false;
          }
     }


     result = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_VOLUME, &mVolume);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj GetInterface volume failed : %d", result);
          return false;
     }

     //第五步 设置播放器状态播放状态
     result = (*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_PLAYING);
     if(result != SL_RESULT_SUCCESS){
          LOGE("mPlayerObj set play state failed : %d", result);
          return false;
     }

     return true;
}

//一帧音频播放完毕后就会回调这个函数
void playerCallback(SLAndroidSimpleBufferQueueItf bq, void *context){
     OpenSLAudioPlay *player = (OpenSLAudioPlay *)context;
     pthread_mutex_unlock(&player->mMutex);
}

void OpenSLAudioPlay::enqueueSample(void *data, size_t length) {
     //必须等待一帧音频播放完毕后才能 enqueue 第二帧音频
     pthread_mutex_lock(&mMutex);
     if(mBufSize < length){
          mBufSize = length;
          if(mBuffers[0]){
               delete [] mBuffers[0];
          }

          if(mBuffers[1]){
               delete [] mBuffers[1];
          }
          mBuffers[0] = new uint8_t[mBufSize];
          mBuffers[1] = new uint8_t[mBufSize];
     }

     memcpy(mBuffers[mIndex], data, length);
     //第六步： 手动激活回调函数
     (*mBufferQueue)->Enqueue(mBufferQueue, mBuffers[mIndex], length);
     mIndex = 1 - mIndex;
}


void OpenSLAudioPlay::release() {

     pthread_mutex_lock(&mMutex);
     if(mPlayerObj){
          (*mPlayerObj)->Destroy(mPlayerObj);
          mPlayerObj = NULL;
          mPlayer = NULL;
          mBufferQueue = NULL;
          mEffectSend = NULL;
          mVolume = NULL;
     }

     if(mAudioEngine){
          delete mAudioEngine;
          mAudioEngine = NULL;
     }

     if(mBuffers[0]){
          delete [] mBuffers[0];
          mAudioEngine = NULL;
     }

     if(mBuffers[1]){
          delete [] mBuffers[1];
          mAudioEngine = NULL;
     }

     pthread_mutex_unlock(&mMutex);
     pthread_mutex_destroy(&mMutex);
}
