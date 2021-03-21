//
// Created by Administrator on 2021/3/20 0020.
//
#ifdef __cplusplus
extern "C" {
#endif

#include <libavcodec/avcodec.h>
#include <libavutil/audio_fifo.h>
#include <libavformat/avformat.h>
#include <libavutil/avstring.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include <libavutil/opt.h>
#include <libavutil/channel_layout.h>
#include <libavutil/samplefmt.h>

#ifdef __cplusplus
}
#endif

#include "../include/base.h"


#define STREAM_FRAME_RATE 25
const int width = 1080;
const int height = 1440;


const int delta = 30;
AVCodec* pCodec;
AVCodecContext* pCodecCtx;
AVFrame* pFrame;
uint8_t* picture_buf;

int allocVideoStream(int width, int height, int bitRate, int frameRate){

    pCodec = avcodec_find_encoder(AV_CODEC_ID_H264);
    if(!pCodec){
        LOGI("Can not find encoder !\n");
        return -1;
    }

    pCodecCtx = avcodec_alloc_context3(pCodec);
    pCodecCtx->pix_fmt = AV_PIX_FMT_YUV420P;
    pCodecCtx->width = width;
    pCodecCtx->height = height;

    pCodecCtx->time_base.num =1;
    pCodecCtx->time_base.den = frameRate;
    pCodecCtx->gop_size = (int)frameRate;
    pCodecCtx->max_b_frames = 0;

    LOGI("******************* gop size is %.2f videoBitRate is %d ************");
    pCodecCtx->flags |= CODEC_FLAG_QSCALE;
    pCodecCtx->i_quant_factor = 0.8;
    pCodecCtx->qmin = 10;
    pCodecCtx->qmax = 30;
    pCodecCtx->bit_rate = bitRate;
    pCodecCtx->rc_min_rate = bitRate - delta * 1000;
    pCodecCtx->rc_max_rate = bitRate + delta * 1000;
    pCodecCtx->rc_buffer_size = bitRate * 2;

    //h.264 设置
    av_opt_set(pCodecCtx->priv_data, "preset", "fast", 0);
    //实时编码
    av_opt_set(pCodecCtx->priv_data, "tune", "zerolatency", 0);
    av_opt_set(pCodecCtx->priv_data, "profile", "main", 0);

    if(avcodec_open2(pCodecCtx, pCodec, NULL) < 0){
        LOGI("failed to open encoder. \n");
        return -1;
    }

    return 0;
}


void allocAVFrame(){
    //target yuv420p buffer.
    pFrame = av_frame_alloc();
    int pictureSize = avpicture_get_size(pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height);
    picture_buf = (uint8_t*) av_malloc(pictureSize);
    avpicture_fill((AVPicture*) pFrame, picture_buf,pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height);
}


void destroy(){
    avcodec_close(pCodecCtx);
    av_free(pFrame);
    av_free(picture_buf);
}


void yuvEncodeH264(const char* input, const char* output){

    //注册所有格式编解码器
    av_register_all();

    if(allocVideoStream(width, height, 400000, STREAM_FRAME_RATE) < 0){
        LOGI("alloc video stream failed...");
        return ;
    }

    allocAVFrame();

    //input: "/storage/emulated/0/Android/data/com.nipuream.audiovideo/cache/yanghui.yuv"
    //output : "/storage/emulated/0/Android/data/com.nipuream.audiovideo/cache/output.h.264"
    FILE* fp = fopen(input, "rb");
    FILE* h264File = fopen(output, "wb");

    while (1){

        if(fread(picture_buf, pCodecCtx->width * pCodecCtx->height *3 /2, 1, fp) == 0){
            break;
        }

        AVPacket pkt = {0};
        int got_packet;
        av_init_packet(&pkt);

        int ret = avcodec_encode_video2(pCodecCtx, &pkt, pFrame, &got_packet);
        if(ret < 0){
            LOGI("Error encoding video frame : %s", av_err2str(ret));
            break;
        } else if(got_packet && pkt.size){
            fwrite(pkt.data, pkt.size, 1, h264File);
        } else {
            LOGI("No output frame...");
            break;
        }

        av_free_packet(&pkt);
    }

    fflush(h264File);
    fclose(fp);
    fclose(h264File);
    destroy();


    LOGI("yuv420p convert to h.264 complete !!!");
}
