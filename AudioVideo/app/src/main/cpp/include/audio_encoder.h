//
// Created by Administrator on 2020/11/29 0029.
//

#ifndef AUDIOVIDEO_AUDIO_ENCODER_H
#define AUDIOVIDEO_AUDIO_ENCODER_H

extern "C" {
#include <avcodec.h>
#include <avformat.h>
#include <avutil.h>
#include <swresample.h>
#include <samplefmt.h>
#include <common.h>
#include <channel_layout.h>
#include <opt.h>
#include <imgutils.h>
#include <mathematics.h>
};

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

class AudioEncoder {

private:
    AVFormatContext* avFormatContext;
    AVCodecContext* avCodecContext;
    AVStream* audioStream;

    bool isWriteHeaderSuccess;
    double duration;

    FILE* testFile;

    AVFrame* input_frame;
    int buffer_size;
    uint8_t* samples;
    int samplesCursor;
    SwrContext* swrContext;
    uint8_t** convert_data;
    AVFrame* swrFrame;
    uint8_t* swrBuffer;
    int swrBufferSize;

    int pushlishBitRate;
    int audioChannels;
    int audioSampleRate;

    int totalSWRTimeMilles;
    int totalEncodeTimeMills;

    //初始化的时候，要进行的工作
    int alloc_acframe();
    int alloc_audio_stream(const char* codec_name);
    //当够了一个frame之后要编码一个packet
    void encodePacket();

    void addADTStoPacket(uint8_t* packet, int packetLen);
    void writeAACPacketToFile(uint8_t* data, int datalen);

public:
    AudioEncoder();
    virtual ~AudioEncoder();

    int init(int bitRate,int channels, int sampleRate, int bitsPerSample,const char* aacFilePath, const char* codec_name);
    int init(int bitRate, int channels, int bitsPerSample, const char* aacFilePath, const char* codec_name);
    void destroy();

};


#define PUBLISH_BYTE_RATE 64000



#endif //AUDIOVIDEO_AUDIO_ENCODER_H
