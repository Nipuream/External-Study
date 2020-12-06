//
// Created by Administrator on 2020/12/5 0005.
//

#include "../include/audio_encoder.h"
#include "../include/base.h"


AudioEncoder::AudioEncoder() {
    LOGI("AudioEncoder init.");
}

AudioEncoder::~AudioEncoder() {
    LOGI("AudioEncoder destroy..");
}


int AudioEncoder::init(int bitRate, int channels, int sampleRate, int bitsPerSample, const char *aacFilePath,
        const char *codec_name) {

    avCodecContext = NULL;
    avFormatContext = NULL;
    input_frame = NULL;
    samples = NULL;
    samplesCursor = 0;
    swrContext = NULL;
    swrBuffer = NULL;
    swrFrame = NULL;
    convert_data = NULL;
    isWriteHeaderSuccess = false;
    totalEncodeTimeMills = 0;
    totalSWRTimeMilles = 0;

    pushlishBitRate = bitRate;
    audioChannels = channels;
    audioSampleRate = sampleRate;

    int ret;
    avcodec_register_all();
    av_register_all();


    avFormatContext = avformat_alloc_context();
    LOGI("acc file path : %s", aacFilePath);

    if((ret = avformat_alloc_output_context2(&avFormatContext,
            NULL, NULL,aacFilePath)) != 0){
        LOGI("avFormatContext alloc failed : %s", av_err2str(ret));
        return -1;
    }


    if(ret = avio_open2(&avFormatContext->pb, aacFilePath, AVIO_FLAG_WRITE, NULL, NULL)){
        LOGI("could not avio open fail %s ", av_err2str(ret));
        return -1;
    }

    alloc_audio_stream(codec_name);

    av_dump_format(avFormatContext, 0, aacFilePath, 1);

    //wirte header.
    if(avformat_write_header(avFormatContext, NULL) != 0){
        LOGI("could not write header \n");
        return -1;
    }

    isWriteHeaderSuccess = true;
    alloc_acframe();
    return 1;
}


void AudioEncoder::encode(byte *buffer, int size) {

    int bufferCursor = 0;
    int bufferSize = size;
    while (bufferSize >= (buffer_size - samplesCursor)) {

        int cpySize = buffer_size - samplesCursor;
        memcpy(samples + samplesCursor, buffer + bufferCursor, cpySize);
        bufferCursor += cpySize;
        bufferSize -= cpySize;
        long long beginEncodeTimeMills = getCurrentTime();
        encodePacket();
        totalEncodeTimeMills += (getCurrentTime() - beginEncodeTimeMills);
        samplesCursor = 0;
    }

    if(bufferSize > 0){
        memcpy(samples + samplesCursor, buffer + bufferCursor, bufferSize);
        samplesCursor += bufferSize;
    }
}

void AudioEncoder::encodePacket() {

    LOGI("start encode package...");

}


int AudioEncoder::alloc_acframe() {

    return 0;
}


int AudioEncoder::alloc_audio_stream(const char *codec_name) {
    return 0;
}

