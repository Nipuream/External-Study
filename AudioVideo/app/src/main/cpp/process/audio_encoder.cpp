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

    publishBitRate = bitRate;
    audioChannels = channels;
    audioSampleRate = sampleRate;

    int ret;
    //将编码器和解码器都放到链表中去
    avcodec_register_all();
    //注册所有config.h里面开放的编解码器
    av_register_all();


    //上下文对象
    avFormatContext = avformat_alloc_context();
    LOGI("acc file path : %s", aacFilePath);

    // 传入输出文件格式，分配出上下文，即分配出封装格式
    if((ret = avformat_alloc_output_context2(&avFormatContext,
            NULL, NULL,aacFilePath)) != 0){
        LOGI("avFormatContext alloc failed : %s", av_err2str(ret));
        return -1;
    }


    //传入AAC的编码路径，打开文件的连接通道
    if(ret = avio_open2(&avFormatContext->pb, aacFilePath, AVIO_FLAG_WRITE, NULL, NULL)){
        LOGI("could not avio open fail %s ", av_err2str(ret));
        return -1;
    }

    testFile = fopen(aacFilePath, "wb+");
    alloc_audio_stream(codec_name);

    //打印详细的信息
    av_dump_format(avFormatContext, 0, aacFilePath, 1);

    //wirte header.
    if(avformat_write_header(avFormatContext, NULL) != 0){
        LOGI("could not write header \n");
        return -1;
    }

    isWriteHeaderSuccess = true;
    alloc_avframe();
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
    int ret, go_output;
    AVPacket pkt;
    av_init_packet(&pkt);
    AVFrame* encode_frame;
    if(swrContext){
        long long beginSWRTimeMills = getCurrentTime();
        swr_convert(swrContext, convert_data, avCodecContext->frame_size,
                    (const uint8_t**)input_frame->data, avCodecContext->frame_size);
        int length = avCodecContext->frame_size * av_get_bytes_per_sample(avCodecContext->sample_fmt);
        for(int k = 0; k < 2; ++k){
            for(int j = 0; j < length; ++j){
                swrFrame->data[k][j] = convert_data[k][j];
            }
        }
        totalSWRTimeMilles += (getCurrentTime() - beginSWRTimeMills);
        encode_frame = swrFrame;
    } else {
        encode_frame = input_frame;
    }

    pkt.stream_index = 0;
    pkt.duration = (int) AV_NOPTS_VALUE;
    pkt.pts = pkt.dts = 0;
    pkt.data = samples;
    pkt.size = buffer_size;
    ret = avcodec_encode_audio2(avCodecContext, &pkt, encode_frame, &go_output);
    if(ret < 0){
        LOGI("Error encoding audio frame\n");
        return ;
    }

    if(go_output){
        writeAACPacketToFile(pkt.data, pkt.size);
        if(avCodecContext->coded_frame && avCodecContext->coded_frame->pts != AV_NOPTS_VALUE){
            pkt.pts = av_rescale_q(avCodecContext->coded_frame->pts, avCodecContext->time_base, audioStream->time_base);
        }
        pkt.flags |= AV_PKT_FLAG_KEY;
        this->duration = pkt.pts * av_q2d(audioStream->time_base);
        //此函数负责交错的输出一个媒体包，如果调用者无法保证来自各个媒体流体包正确交错，则最好调动此函数输出媒体包，反之，可以调动av_write_frame以提高性能
        int writeCode = av_interleaved_write_frame(avFormatContext, &pkt);
    }

    av_free_packet(&pkt);
}


void AudioEncoder::addADTStoPacket(uint8_t *packet, int packetLen) {
    int profile = 29;  //2: LC 5:HE-AAC 29:HEV2
    int freqIdx = 3; //48khz
    int chanCfg = 1; //Mono

    //file in ADTS data.
    packet[0] = (unsigned char) 0xFF;
    packet[1] = (unsigned char) 0xF1;
    packet[2] = (unsigned char) 0x58;//(unsigned char) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
    packet[3] = (unsigned char) (((chanCfg & 3) << 6) + (packetLen >> 11));
    packet[4] = (unsigned char) ((packetLen & 0x7FF) >> 3);
    packet[5] = (unsigned char) (((packetLen & 7) << 5) + 0x1F);
    packet[6] = (unsigned char) 0xFC;

}

void AudioEncoder::writeAACPacketToFile(uint8_t *data, int datalen) {

    LOGI("After one encode Size is : %d, ", datalen);
    uint8_t * buffer = new uint8_t[datalen + 7];
    memset(buffer, 0, datalen + 7);
    memcpy(buffer + 7, data, datalen);
    addADTStoPacket(buffer, datalen + 7);
    fwrite(buffer, sizeof(uint8_t), datalen + 7, testFile);
    delete[] buffer;
}

int AudioEncoder::alloc_avframe() {

    int ret = 0;
    AVSampleFormat preferedSampleFMT = AV_SAMPLE_FMT_S16;
    int preferedChannels = audioChannels;
    int preferedSampleRate = audioSampleRate;
    input_frame = av_frame_alloc();

    if(!input_frame){
        LOGI("Could not allocate audio frame\n");
        return -1;
    }

    input_frame->nb_samples = avCodecContext->frame_size;
    input_frame->format = preferedSampleFMT;
    input_frame->channel_layout = preferedChannels == 1 ? AV_CH_LAYOUT_MONO : AV_CH_LAYOUT_STEREO;
    input_frame->sample_rate = preferedSampleRate;
    buffer_size = av_samples_get_buffer_size(NULL,
            av_get_channel_layout_nb_channels(input_frame->channel_layout),
            input_frame->nb_samples,
            preferedSampleFMT,0);
    samples = (uint8_t*)av_malloc(buffer_size);
    samplesCursor = 0;
    if(!samples){
        LOGI("Could not allocated %d, bytes for samples buffer\n", buffer_size);
        return -2;
    }

    LOGI("allocate %d bytes for samples buffer\n", buffer_size);
    //setup the data pointers in the AVFrame.
    ret = avcodec_fill_audio_frame(input_frame, av_get_channel_layout_nb_channels(input_frame->channel_layout),
             preferedSampleFMT, samples, buffer_size, 0);
    if(ret < 0){
        LOGI("Could not setup audio frame\n");
    }

    if(swrContext){
        if(av_sample_fmt_is_planar(avCodecContext->sample_fmt)){
            LOGI("Codec Context SampleFormat is Planar...");
        }
        //分配空间
        convert_data = (uint8_t**)(calloc(avCodecContext->channels,
                                                      sizeof(*convert_data)));

        av_samples_alloc(convert_data, NULL, avCodecContext->channels, avCodecContext->frame_size,
                            avCodecContext->sample_fmt,0);
        swrBufferSize = av_samples_get_buffer_size(NULL, avCodecContext->channels, avCodecContext->frame_size, avCodecContext->sample_fmt, 0);
        swrBuffer = (uint8_t*)av_malloc(swrBufferSize);
        LOGI("After av_malloc swrBuffer");
        //此时 data[0], data[1] 分别指向 frame_buf 数组起始， 中间地址
        swrFrame = av_frame_alloc();
        if(!swrFrame){
            LOGI("Could not allocate swrFrame frame \n");
            return -1;
        }

        swrFrame->nb_samples = avCodecContext->frame_size;
        swrFrame->format = avCodecContext->sample_fmt;
        swrFrame->channel_layout = avCodecContext->channels == 1 ? AV_CH_LAYOUT_MONO : AV_CH_LAYOUT_STEREO;
        swrFrame->sample_rate = avCodecContext->sample_rate;
        ret = avcodec_fill_audio_frame(swrFrame, avCodecContext->channels, avCodecContext->sample_fmt,(const uint8_t*)swrBuffer, swrBufferSize, 0);
        LOGI("After avcodec_fill_audio_frame");
        if(ret < 0){
            LOGI("avcodec_fill_audio_frame error");
            return -1;
        }
    }

    return ret;
}


int AudioEncoder::alloc_audio_stream(const char *codec_name) {

    AVCodec *codec;
//    testFile = fopen("sdcard/Android/yanghui.pcm", "wb+");
    AVSampleFormat  preferedSampleFMT = AV_SAMPLE_FMT_S16;
    int preferedChannels = audioChannels;
    int preferedSampleRate = audioSampleRate;

    //填充一轨 AVStream
    audioStream = avformat_new_stream(avFormatContext, NULL);
    audioStream->id = 1;
    avCodecContext = audioStream->codec;
    avCodecContext->codec_type = AVMEDIA_TYPE_AUDIO; //表示音频类型
    avCodecContext->sample_rate = audioSampleRate;
    if(publishBitRate  > 0){
        avCodecContext->bit_rate = publishBitRate;
    } else {
        avCodecContext->bit_rate = PUBLISH_BITE_RATE;
    }

    avCodecContext->sample_fmt = preferedSampleFMT;  //如何数字化,  AV_SAMPLE_FMT_S16 就用一个short表示一个采样点
    LOGI("audioChannels is %d", audioChannels);
    //channel_layout 和 channels 意义差不多
    avCodecContext->channel_layout = preferedChannels == 1 ? AV_CH_LAYOUT_MONO : AV_CH_LAYOUT_STEREO;
    avCodecContext->channels = av_get_channel_layout_nb_channels(avCodecContext->channel_layout);

    avCodecContext->profile = FF_PROFILE_AAC_HE;
    LOGI("avCodecContext->channels is %d", avCodecContext->channels);
    avCodecContext->flags |= CODEC_FLAG_GLOBAL_HEADER;

    //找出对应的编码器
    codec = avcodec_find_encoder_by_name(codec_name);
    if(!codec){
        LOGI("Couldn't find a valid audio codec");
        return -1;
    }

    avCodecContext->codec_id = codec->id;

    //确定采样格式
    if(codec->sample_fmts){

        const enum AVSampleFormat *p = codec->sample_fmts;
        for(; *p != -1; p++){
            if(*p == audioStream->codec->sample_fmt){
                break;
            }
        }

        if(*p == -1){
            LOGI("sample format incompatible with codec, Defaulting to a format known to work.");
            avCodecContext->sample_fmt = codec->sample_fmts[0];
        }
    }

    //确定采样率
    if(codec->supported_framerates){
        const int *p = codec->supported_samplerates;
        int best = 0;
        int best_dist = INT_MAX;
        for(; *p; p++){
            int dist = abs(audioStream->codec->sample_rate - *p);
            if(dist < best_dist){
                best_dist = dist;
                best = *p;
            }
        }

        avCodecContext->sample_rate = best;
    }

    //某些编码器只允许特定格式的pcm作为输入源 需要构造一个重采样器将PCM数据转化为可适配编码器输入的PCM数据
    if ( preferedChannels != avCodecContext->channels
         || preferedSampleRate != avCodecContext->sample_rate
         || preferedSampleFMT != avCodecContext->sample_fmt) {
        LOGI("channels is {%d, %d}", preferedChannels, audioStream->codec->channels);
        LOGI("sample_rate is {%d, %d}", preferedSampleRate, audioStream->codec->sample_rate);
        LOGI("sample_fmt is {%d, %d}", preferedSampleFMT, audioStream->codec->sample_fmt);
        LOGI("AV_SAMPLE_FMT_S16P is %d AV_SAMPLE_FMT_S16 is %d AV_SAMPLE_FMT_FLTP is %d", AV_SAMPLE_FMT_S16P, AV_SAMPLE_FMT_S16, AV_SAMPLE_FMT_FLTP);
        //设置重采样上下文
        swrContext = swr_alloc_set_opts(NULL,
                                        av_get_default_channel_layout(avCodecContext->channels),
                                        (AVSampleFormat)avCodecContext->sample_fmt, avCodecContext->sample_rate,
                                        av_get_default_channel_layout(preferedChannels),
                                        preferedSampleFMT, preferedSampleRate,
                                        0, NULL);
        if (!swrContext || swr_init(swrContext)) {
            if (swrContext)
                swr_free(&swrContext);
            return -1;
        }
    }

    //打开编码器,为编码器指定 frame_size 大小，一般1024作为一帧的大小
    if (avcodec_open2(avCodecContext, codec, NULL) < 0) {
        LOGI("Couldn't open codec");
        return -2;
    }
    avCodecContext->time_base.num = 1;
    avCodecContext->time_base.den = avCodecContext->sample_rate;
    avCodecContext->frame_size = 1024;
    return 0;
}

void AudioEncoder::destroy() {
    LOGI("AudioEncoder destroy...");
}

void encodeAACUsingFFmpeg(const char* pcmPath, const char* aacPath,
                          int channels, int bitRate, int sampleRate){

    AudioEncoder* audioEncoder = new AudioEncoder();
    int bitsPerSample = 16; //量化格式
    char* codec_name = "libfdk_aac";
    LOGI("before audioEncode init...");
    audioEncoder->init(bitRate, channels, sampleRate, bitsPerSample, aacPath, codec_name);
    int bufferSize = 1024 * 256;
    byte* buffer = new byte[bufferSize];

    FILE* pcmFileHandle = fopen(pcmPath, "rb+");
    int readBufferSize = 0;

    while((readBufferSize = fread(buffer, 1, bufferSize, pcmFileHandle)) > 0){
        LOGI("read buffer size : %d", readBufferSize);
        audioEncoder->encode(buffer, readBufferSize);
    }
    LOGI("After Encode");
    delete[] buffer;
    fclose(pcmFileHandle);
    audioEncoder->destroy();
    delete audioEncoder;
}

