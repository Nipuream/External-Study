//
// Created by Administrator on 2021/1/30 0030.
//

#ifndef AUDIOVIDEO_OPENSL_AUDIO_PLAYER_H
#define AUDIOVIDEO_OPENSL_AUDIO_PLAYER_H

#include "audio_engine.h"

#define SAMPLE_FORMAT_16 16

class OpenSLAudioPlay {

private:
    AudioEngine *mAudioEngine;
    SLObjectItf mPlayerObj;
    SLPlayItf mPlayer;
    SLAndroidSimpleBufferQueueItf  mBufferQueue;
    SLEffectSendItf  mEffectSend;
    SLVolumeItf  mVolume;
    SLmilliHertz mSampleRate;
    int mSampleFormat;
    int mChannels;

    uint8_t  *mBuffers[2];
    SLuint32 mBufSize;
    int mIndex;
    pthread_mutex_t  mMutex;

public:
    OpenSLAudioPlay(int sampleRate, int sampleFormat, int channels);
    bool init();
    void enqueueSample(void *data, size_t length);
    void release();
    ~OpenSLAudioPlay();

    friend void playerCallback(SLAndroidSimpleBufferQueueItf bq, void* context);
};


#endif //AUDIOVIDEO_OPENSL_AUDIO_PLAYER_H
