package com.nipuream.audiovideo.audio.task;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 从网络读取音频帧 处理方式
 */
public class DecodeAACTask implements Runnable{

    private static final String TAG = DecodeAACTask.class.getName();
    //采样频率一般分为 22.05khz 44.1khz 48khz
    private final static int AUDIO_SAMPLE_RATE = 44100;
    //用来记录解码失败的帧数
    private int count = 0;
    //解码器
    private MediaCodec mDecoder;
    //解码后的文件
    private FileOutputStream fos;
    //一般AAC帧大小不超过200k,如果解码失败可以尝试增大这个值
    private static int FRAME_MAX_LEN = 100 * 1024;
    //文件读取完成标识
    private boolean isFinish = false;
    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private int PRE_FRAME_TIME = 1000 / 50;
    //这个值用于找到第一个帧头后，继续寻找第二个帧头，如果解码失败可以尝试缩小这个值
    private int FRAME_MIN_LEN = 50;

    public DecodeAACTask(MediaCodec mDecoder){
        try {
            fos = new FileOutputStream("/sdcard/Android/decode_aac.pcm", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        prepareDecode();
    }

    private void prepareDecode(){

        //需要解码数据的类型
        String mine = "audio/mp4a-latm";
        //初始化解码器
        try {
            mDecoder = MediaCodec.createDecoderByType(mine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //MediaFormat 用于描述音视频数据的相关参数
        MediaFormat mediaFormat = new MediaFormat();
        //数据类型
        mediaFormat.setString(MediaFormat.KEY_MIME, mine);
        //声道个数
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        //采样率
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, AUDIO_SAMPLE_RATE);
        //比特率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, AUDIO_SAMPLE_RATE * 16 * 1);
        //用来标记AAC是否有adts，1->有
        mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);
        //用来标记aac类型
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        //ByteBuffer key
        byte[] data = new byte[]{0x11, (byte)0x90};
        ByteBuffer csd_0 = ByteBuffer.wrap(data);
        mediaFormat.setByteBuffer("csd-0", csd_0);
        //编码器配置
        mDecoder.configure(mediaFormat, null, null, 0);
    }


    public void decode(byte[] buf, int offset, int length){

        //输入ByteBuffer
        ByteBuffer[] codecInputBuffers = mDecoder.getInputBuffers();
        //输出ByteBuffer
        ByteBuffer[] codecOutputBuffers = mDecoder.getOutputBuffers();
        //等待时间, 0->不等待 -1->一直等待
        long kTimeOutUs = 0;
        try{
            //返回一个包含有效数据的input buffer的index, -1 -> 不存在
            int inputBufIndex = mDecoder.dequeueInputBuffer(kTimeOutUs);
            if(inputBufIndex > 0){
                //获取当前的ByteBuffer
                ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                //清空ByteBuffer
                dstBuf.clear();
                //填充数据
                dstBuf.put(buf, offset, length);
                //将指定index的input buffer提交给解码器
                mDecoder.queueInputBuffer(inputBufIndex, 0, length, 0, 0);
            }

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            //返回一个output buffer的index， -1-> 不存在
            int outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs);

            if(outputBufferIndex < 0){
                //记录解码失败的次数
                count ++;
            }

            ByteBuffer outputBuffer;
            while (outputBufferIndex >=0){
                //获取解码后的 ByteBuffer
                outputBuffer = codecOutputBuffers[outputBufferIndex];
                //用来保存解码后的数据
                byte[] outData = new byte[info.size];
                outputBuffer.get(outData);

                //清空缓存
                outputBuffer.clear();
                //播放解码后的数据

                fos.write(outData, 0, info.size);
            }

            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(fos != null){
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void run() {

        if(mDecoder != null){
            mDecoder.start();
        } else {
            Log.i(TAG, "MediaCodec is null");
            return ;
        }

        if(fos == null){
            Log.i(TAG,"decode pcm file is not exits.");
            return ;
        }

        //读取aac 文件
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/sdcard/Android/output.aac");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(fis == null){
            Log.i(TAG,"Can't find input aac path.");
            return ;
        }

        //保存完整数据帧
        byte[] frame = new byte[FRAME_MAX_LEN];
        //当前帧长度
        int frameLen = 0;
        //每次从文件读取的数据
        byte[] readData = new byte[10 * 1024];
        //开始时间
        long startTime = System.currentTimeMillis();

        //不断读取数据
        try{
            //循环读取数据
            while (!isFinish) {
                if (fis.available() > 0) {
                    int readLen = fis.read(readData);
                    //当前长度小于最大值
                    if (frameLen + readLen < FRAME_MAX_LEN) {
                        //将readData拷贝到frame
                        System.arraycopy(readData, 0, frame, frameLen, readLen);
                        //修改frameLen
                        frameLen += readLen;
                        //寻找第一个帧头
                        int headFirstIndex = findHead(frame, 0, frameLen);
                        while (headFirstIndex >= 0 && isHead(frame, headFirstIndex)) {
                            //寻找第二个帧头
                            int headSecondIndex = findHead(frame, headFirstIndex + FRAME_MIN_LEN, frameLen);
                            //如果第二个帧头存在，则两个帧头之间的就是一帧完整的数据
                            if (headSecondIndex > 0 && isHead(frame, headSecondIndex)) {
                                //视频解码
                                count++;
                                Log.e("ReadAACFileThread", "Length : " + (headSecondIndex - headFirstIndex));
//                                audioUtil.decode(frame, headFirstIndex, headSecondIndex - headFirstIndex);
                                //截取headSecondIndex之后到frame的有效数据,并放到frame最前面
                                byte[] temp = Arrays.copyOfRange(frame, headSecondIndex, frameLen);
                                System.arraycopy(temp, 0, frame, 0, temp.length);
                                //修改frameLen的值
                                frameLen = temp.length;
                                //线程休眠
                                sleepThread(startTime, System.currentTimeMillis());
                                //重置开始时间
                                startTime = System.currentTimeMillis();
                                //继续寻找数据帧
                                headFirstIndex = findHead(frame, 0, frameLen);
                            } else {
                                //找不到第二个帧头
                                headFirstIndex = -1;
                            }
                        }
                    } else {
                        //如果长度超过最大值，frameLen置0
                        frameLen = 0;
                    }
                } else {
                    //文件读取结束
                    isFinish = true;
                }
            }


        }catch (Exception e){
                e.printStackTrace();
        }
    }

    /**
     * 寻找指定buffer中AAC帧头的开始位置
     *
     * @param startIndex 开始的位置
     * @param data       数据
     * @param max        需要检测的最大值
     * @return
     */
    private int findHead(byte[] data, int startIndex, int max) {
        int i;
        for (i = startIndex; i <= max; i++) {
            //发现帧头
            if (isHead(data, i))
                break;
        }
        //检测到最大值，未发现帧头
        if (i == max) {
            i = -1;
        }
        return i;
    }

    /**
     * 判断aac帧头
     */
    private boolean isHead(byte[] data, int offset) {
        boolean result = false;
        if (data[offset] == (byte) 0xFF && data[offset + 1] == (byte) 0xF1
                && data[offset + 3] == (byte) 0x80) {
            result = true;
        }
        return result;
    }

    //修眠
    private void sleepThread(long startTime, long endTime) {
        //根据读文件和解码耗时，计算需要休眠的时间
        long time = PRE_FRAME_TIME - (endTime - startTime);
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
