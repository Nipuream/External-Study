#include <jni.h>
#include "include/base.h"
#include <libyuv.h>
#include "include/opensl_audio_player.h"
#include "preview/mv_recording_preview_controller.h"
#include <sys/types.h>

ANativeWindow* nativeWindow;
extern void yuvEncodeH264(const char* input, const char* output);

extern "C" JNIEXPORT jstring JNICALL
Java_com_nipuream_audiovideo_NativeLib_stringFromJNI(
        JNIEnv* env,
        jclass clazz /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_playYUV(
        JNIEnv* env,
        jclass clazz,
        jstring jstr){

    const char* path = env->GetStringUTFChars(jstr, 0);
    LOGI("receive Java yuv path : %s", path);
    pthread_t  pid;
    pthread_create(&pid, NULL, readYUVWithOpenGl, (void *)path);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_MainActivity_showBitmap(
        JNIEnv* env,
        jobject instace
){
    jclass  clazz = env->FindClass("com/nipuream/audiovideo/MainActivity");
    jfieldID  bitmapJava = env->GetFieldID(clazz,"bitmap", "Landroid/graphics/Bitmap;");
    jobject  bitmapNative = env->GetObjectField(instace, bitmapJava);

    //Get image info
    AndroidBitmapInfo info;
    if(AndroidBitmap_getInfo(env, bitmapNative, &info) != 0){
        LOGE("AndroidBitmap_getInfo failed ...");
        return ;
    }

    //Check image.
    if(info.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
        LOGE("Bitmap format is not RGBA_8888");
        return ;
    }

    //Get image addr.
    void* addr;
    int getRes = AndroidBitmap_lockPixels(env, bitmapNative, &addr);
    if(getRes != 0){
        LOGE("AndroidBitmap_lockPixels() failed !");
        return ;
    }

    loadImage(addr, info);
    AndroidBitmap_unlockPixels(env, bitmapNative);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_MainActivity_setSurface(
        JNIEnv* env,
        jobject instance,
        jobject surface
){

    LOGI("Get surface...");

    if(nativeWindow){
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
    }

    nativeWindow = ANativeWindow_fromSurface(env, surface);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_scale(
        JNIEnv* env,
        jclass clazz,
        jbyteArray src_,
        jbyteArray dst_,
        jint srcWidth,
        jint srcHeight,
        jint dstWidth,
        jint dstHeight
){

    LOGI("image scale, srcWidth : %d, srcHeight : %d, dstWidth : %d, dstHeight : %d", srcWidth, srcHeight, dstWidth, dstHeight);
    jbyte* data = env->GetByteArrayElements(src_,0);
    uint8_t* src = reinterpret_cast<uint8_t *>(data);
    int64_t size = (dstWidth * dstHeight * 3) >> 1;
    uint8_t dst[size];

    uint8_t *src_y;
    int src_stride_y;
    uint8_t *src_u;
    int src_stride_u;
    uint8_t *src_v;
    int src_stride_v;

    uint8_t *dst_y;
    int dst_stride_y;
    uint8_t *dst_u;
    int dst_stride_u;
    uint8_t *dst_v;
    int dst_stride_v;

    src_stride_y = srcWidth;
    src_stride_u = srcWidth >> 1;
    src_stride_v = src_stride_u;

    dst_stride_y = dstWidth;
    dst_stride_u = dstWidth >> 1;
    dst_stride_v = dst_stride_u;

    int src_y_size = srcWidth * srcHeight;
    int src_u_size = src_stride_u * (srcHeight >> 1);
    src_y = src;
    src_u = src + src_y_size;
    src_v = src + src_y_size + src_u_size;

    int dst_y_size = dstWidth * dstHeight;
    int dst_u_size = dst_stride_u * (dstHeight >> 1);
    dst_y = dst;
    dst_u = dst + dst_y_size;
    dst_v = dst + dst_y_size + dst_u_size;

    libyuv::I420Scale(src_y, src_stride_y,
                      src_u, src_stride_u,
                      src_v, src_stride_v,
                      srcWidth, srcHeight,
                      dst_y, dst_stride_y,
                      dst_u, dst_stride_u,
                      dst_v, dst_stride_v,
                      dstWidth, dstHeight,
                      libyuv::FilterMode::kFilterNone);

    env->ReleaseByteArrayElements(src_, data, 0);
    env->SetByteArrayRegion(dst_,0, size, reinterpret_cast<const jbyte *>(dst));
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_rotation(
        JNIEnv* env,
        jclass clazz,
        jbyteArray array,
        jint width,
        jint height,
        jint degress

){

    LOGI("image rotate, width : %d, height : %d, degress : %d", width, height, degress);

    jbyte* data = env->GetByteArrayElements(array, 0);
    uint8_t* src = reinterpret_cast<uint8_t *>(data);

    int ysize = width * height;
    int usize = (width >> 1) * (height >> 1);
    int size = (ysize * 3) >> 1;

    uint8_t* src_y = src;
    uint8_t* src_u = src + ysize;
    uint8_t* src_v = src + ysize + usize;

    uint8_t dst[size];
    uint8_t* dst_y = dst;
    uint8_t* dst_u = dst + ysize;
    uint8_t* dst_v = dst + ysize + usize;


    libyuv::I420Rotate(src_y, width, src_u, width >> 1, src_v, width >> 1,
                       dst_y, height, dst_u, height >> 1, dst_v, height >> 1, width, height,
                       static_cast<libyuv::RotationMode>(degress));

    jbyteArray result = env->NewByteArray(size);
    env->SetByteArrayRegion(result,0,size, reinterpret_cast<const jbyte *>(dst));

    env->ReleaseByteArrayElements(array,data,0);
    env->SetByteArrayRegion(array,0,size, reinterpret_cast<const jbyte*>(dst));
}

void* readYUVWithOpenGl(void *pVoid){
    const char* path = reinterpret_cast<const char*>(pVoid);
    drawWithOpenGl(path);
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_encodeAAC(JNIEnv* env, jclass clazz,jstring pcmPathParam,jint channels, jint bitRate, jint sampleRate, jstring aacPathParam){

    const char* pcmPath = env->GetStringUTFChars(pcmPathParam, NULL);
    const char* aacPath = env->GetStringUTFChars(aacPathParam, NULL);
    encodeAACUsingFFmpeg(pcmPath, aacPath, channels, bitRate, sampleRate);
    env->ReleaseStringUTFChars(aacPathParam, aacPath);
    env->ReleaseStringUTFChars(pcmPathParam, pcmPath);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_encodeH264(JNIEnv* env, jclass clazz,jstring input, jstring output){

    const char* input_path = env->GetStringUTFChars(input, NULL);
    const char* output_path = env->GetStringUTFChars(output, NULL);
    yuvEncodeH264(input_path, output_path);
    env->ReleaseStringUTFChars(input, input_path);
    env->ReleaseStringUTFChars(output, output_path);
}


//============================================ OPENSL ES ======================================

FILE *pcmFile = 0;
bool isPlaying = false;
OpenSLAudioPlay *sIAudioPlayer = NULL;
void closeOpenSl();


void *playThreadFunc(void* arg){
    const int bufferSize = 2048;
    short buffer[bufferSize];
    while (isPlaying && !feof(pcmFile)){
        fread(buffer, 1, bufferSize, pcmFile);
        sIAudioPlayer->enqueueSample(buffer, bufferSize);
    }
    closeOpenSl();
    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_NativeLib_playPcmWithSL(JNIEnv* env, jclass clazz, jstring pcmPath){

    const char* _pcmPath = env->GetStringUTFChars(pcmPath, NULL);

    if(sIAudioPlayer){
        sIAudioPlayer->release();
        delete sIAudioPlayer;
        sIAudioPlayer = NULL;
    }

    //实例化 OpenSlAudioPlay
    sIAudioPlayer = new OpenSLAudioPlay(44100, SAMPLE_FORMAT_16, 1);
    sIAudioPlayer->init();


    pcmFile = fopen(_pcmPath, "r");
    isPlaying = true;
    pthread_t  playThread;
    pthread_create(&playThread, NULL, playThreadFunc, 0);

    env->ReleaseStringUTFChars(pcmPath, _pcmPath);
}


void closeOpenSl(){
    isPlaying = false;
    if(sIAudioPlayer){
        sIAudioPlayer->release();
        delete sIAudioPlayer;
        sIAudioPlayer = NULL;
    }

    if(pcmFile){
        fclose(pcmFile);
        pcmFile = NULL;
    }
}





//============================================ OPENSL ES ======================================


static MVRecordingPreviewController *previewControl = 0;

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_startEncoding(JNIEnv *env,
                                                                           jobject thiz, jint width,
                                                                           jint height,
                                                                           jint video_bit_rate,
                                                                           jint frame_rate,
                                                                           jboolean use_hard_ware_encoding,
                                                                           jstring output_path) {

    if(previewControl != NULL){
        const char* h264FilePath = env->GetStringUTFChars(output_path, NULL);
        previewControl->startEncoding(h264FilePath, width, height, video_bit_rate, frame_rate, use_hard_ware_encoding);
        env->ReleaseStringUTFChars(output_path, h264FilePath);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_stopEncoding(JNIEnv *env,
                                                                          jobject thiz) {

    if(previewControl != NULL){
        previewControl->stopEncoding();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_switchCameraFacing(JNIEnv *env,
                                                                                jobject thiz) {
    if(previewControl != NULL){
        previewControl->switchCameraFacing();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_prepareEGLContext(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jobject surface,
                                                                               jint width,
                                                                               jint height,
                                                                               jint camera_facing_id) {

    previewControl = new MVRecordingPreviewController();
    JavaVM *g_jvm = NULL;
    env->GetJavaVM(&g_jvm);
    jobject  g_obj = env->NewGlobalRef(thiz);
    if(surface != 0 && previewControl != NULL){
        ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
        if(window != NULL){
            previewControl->prepareEGLContext(window, g_jvm, g_obj, width, height, camera_facing_id);
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_createWindowSurface(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jobject surface) {

    if(surface != 0 && previewControl != NULL){
        ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
        if(window != NULL){
            previewControl->createWindowSurface(window);
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_resetRenderSize(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jint width,
                                                                             jint height) {
    if(previewControl != NULL){
        previewControl->resetRenderSize(width, height);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_destroyWindowSurface(JNIEnv *env,
                                                                                  jobject thiz) {
    if(previewControl != NULL){
        previewControl->destroyWindowSurface();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_destroyEGLContext(JNIEnv *env,
                                                                               jobject thiz) {
    if(previewControl != NULL){
        previewControl->destroyEGLContext();
        delete previewControl;
        previewControl = NULL;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nipuream_audiovideo_video_RecordingPreviewScheduler_notifyFrameAvailable(JNIEnv *env,
                                                                                  jobject thiz) {
    if(previewControl != NULL){
        previewControl->notifyFrameAvailable();
    }
}