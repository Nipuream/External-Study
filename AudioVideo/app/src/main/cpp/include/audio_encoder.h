//
// Created by Administrator on 2020/11/29 0029.
//

#ifndef AUDIOVIDEO_AUDIO_ENCODER_H
#define AUDIOVIDEO_AUDIO_ENCODER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libswresample/swresample.h>
#include <libavutil/samplefmt.h>
#include <libavutil/channel_layout.h>
#include <libavutil/opt.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>

#ifdef __cplusplus
} // endof extern "C"
#endif

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "common.h"

#ifndef PUBLISH_BITE_RATE
#define PUBLISH_BITE_RATE 64000
#endif

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

    int publishBitRate;
    int audioChannels;
    int audioSampleRate;

    int totalSWRTimeMilles;
    int totalEncodeTimeMills;

    //初始化的时候，要进行的工作
    int alloc_avframe();
    int alloc_audio_stream(const char* codec_name);
    //当够了一个frame之后要编码一个packet
    void encodePacket();

    void addADTStoPacket(uint8_t* packet, int packetLen);
    void writeAACPacketToFile(uint8_t* data, int datalen);

public:
    AudioEncoder();
    virtual ~AudioEncoder();

    /**
     *
     * @param bitRate  最终编码出来文件的码率
     * @param channels 声道数
     * @param sampleRate 采样率
     * @param bitsPerSample 一帧多少字节
     * @param aacFilePath 编码文件路径
     * @param codec_name 编码器名字
     * @return
     */
    int init(int bitRate,int channels, int sampleRate, int bitsPerSample,const char* aacFilePath, const char* codec_name);
    void encode(byte* buffer, int size);
    void destroy();

};



/**
 * 使用ffempeg编码aac
 * @param pcmPath
 * @param aacPath
 * @param channels
 * @param bitRate
 * @param sampleRate
 */
void encodeAACUsingFFmpeg(const char* pcmPath, const char* aacPath,
        int channels, int bitRate, int sampleRate);


#endif //AUDIOVIDEO_AUDIO_ENCODER_H
