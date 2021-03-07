//
// Created by Administrator on 2021/3/7 0007.
//

#ifndef AUDIOVIDEO_VIDEO_ENCODER_ADAPTER_H
#define AUDIOVIDEO_VIDEO_ENCODER_ADAPTER_H


#include "../../libcommon/CommonTools.h"
#include "../../libcommon/opengl_media/render/video_gl_surface_render.h"
#include "../../libcommon/egl_core/egl_core.h"


class VideoEncoderAdapter {

public:
    VideoEncoderAdapter();
    virtual ~VideoEncoderAdapter();
    virtual void init(const char* h264Path, int width, int height, int videoBitRate, float frameRate);
    virtual void encode() = 0;
    virtual void destroyEncoder() = 0;


protected:
    int encodedFrameCount;
    int videoWidth;
    int videoHeight;
    int videoBitRate;
    float frameRate;
    FILE* h264File;
    int64_t startTime;

    VideoGLSurfaceRender* render;
    int texId;
};



#endif //AUDIOVIDEO_VIDEO_ENCODER_ADAPTER_H
