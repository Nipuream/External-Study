package com.nipuream.audiovideo.audio.task;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DecodeAAC1Task implements Runnable{

    private static final String TAG = DecodeAAC1Task.class.getName();
    private String aacFile, pcmFile;

    public DecodeAAC1Task(String aacFile, String pcmFile){
        this.aacFile = aacFile;
        this.pcmFile = pcmFile;
    }


    @Override
    public void run() {

        File input = new File(aacFile);
        File output = new File(pcmFile);

        if(!input.exists()){
            Log.w(TAG,"aac file not exits.");
            return;
        }

        try {
            decodeAacToPcm(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * AAC 格式解码成 PCM 数据
     *
     * @param aacFile
     * @param pcmFile
     * @throws IOException
     */
    public void decodeAacToPcm(File aacFile, File pcmFile) throws IOException {

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(aacFile.getAbsolutePath());
        MediaFormat mediaFormat = null;
        MediaCodec mediaCodec = null;

        try{
            extractor.setDataSource(aacFile.toString());
        }catch (Exception e){
            e.printStackTrace();
            try{
                extractor.setDataSource(new FileInputStream(aacFile.toString()).getFD());
            }catch (Exception e1){
                e1.toString();
            }
        }

        //获取音频格式轨信息
        mediaFormat = extractor.getTrackFormat(0);

        //从音频格式轨信息中读取采样率 声道数 时长 音频文件类型
        int simpleRate = mediaFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE) : 44100;
        int channelCount = mediaFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        long duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(MediaFormat.KEY_DURATION) : 0;
        String mine = mediaFormat.containsKey(MediaFormat.KEY_MIME) ? mediaFormat.getString(MediaFormat.KEY_MIME) : "";

        Log.i(TAG,"track info : mine : "+ mine + ", sampleRate : "+ simpleRate + ", channels : "+ channelCount + ", duration : "+ duration);

        if(duration <= 0){
            Log.i(TAG,"duration : "+ duration);
            return ;
        }

        //创业一个解码器
        try{
            mediaCodec = MediaCodec.createDecoderByType(mine);
            mediaCodec.configure(mediaFormat, null, null, 0);
        }catch (Exception e){
            e.printStackTrace();
            return ;
        }


        //初始化解码状态，未解析完成
        boolean decodeInputEnd = false;
        boolean decodeOutputEnd = false;

        //当前读取采样数据的大小
        int sampleDataSize;
        //当前输入数据的ByteBuffer序号，当前输出数据的ByteBuffer序号
        int inputBufferIndex;
        int outputBufferIndex;
        //音频文件的采样位数字数 = 采样位数 / 8
        int byteNumber;

        //上一次的解码操作时间，当前解码操作时间， 用于通知回调接口
        long decodeNoticeTime = System.currentTimeMillis();
        long decodeTime;

        //当前采样的音频时间，比如在当前音频的第40秒的时候
        long presentationTimeUs = 0;

        //定义编解码的超时时间
        final long timeOutUs = 10000;

        //存储输入数据的ByteBuffer 数组，输出数据的ByteBuffer数组
        ByteBuffer[] inputBuffers;
        ByteBuffer[] outputBuffers;

        //当前编解码器操作的  输入数据ByteBuffer 和输出数据ByteBuffer,可以从targetBuffer中获取解码后的pcm数据
        ByteBuffer sourceBuffer;
        ByteBuffer targetBuffer;

        //获取输出音频的媒体格式信息
        MediaFormat outputFormat = mediaCodec.getOutputFormat();

        MediaCodec.BufferInfo bufferInfo;
        byteNumber = (outputFormat.containsKey("bit-width") ? outputFormat.getInteger("bit-width") : 0) / 8;
        Log.i(TAG,"byteNumber : "+ byteNumber);

        //开始解码操作
        mediaCodec.start();

        //获取存储输入数据的ByteBuffer数组，输出数据的ByteBuffer数组
        inputBuffers = mediaCodec.getInputBuffers();
        outputBuffers = mediaCodec.getOutputBuffers();

        extractor.selectTrack(0);

        //当前解码的缓存信息，里面的有效数据在offset和offset + size 之间
        bufferInfo = new MediaCodec.BufferInfo();

        //获取解码后文件的输出流
        BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(new FileOutputStream(pcmFile));

        //开始进入循环解码操作，判断读入源音频数据是否完成，输出解码音频数据是否完成
        while (!decodeOutputEnd){

            if(decodeInputEnd){
                return ;
            }

            decodeTime = System.currentTimeMillis();

            try{
                //操作解码输入数据
                //从队列中获取当前解码器处理输入数据的ByteBuffer序号
                inputBufferIndex = mediaCodec.dequeueInputBuffer(timeOutUs);

                if(inputBufferIndex >= 0){
                    //获取当前解码器处理输入数据的ByteBuffer
                    sourceBuffer = inputBuffers[inputBufferIndex];
                    //获取当前ByteBuffer，编解码读取了多少采样数据
                    sampleDataSize = extractor.readSampleData(sourceBuffer, 0);

                    //如果当前读取的采样数据 < 0, 说明已经完成了读取操作
                    if(sampleDataSize < 0){
                        decodeInputEnd = true;
                        sampleDataSize = 0;
                    } else {
                        presentationTimeUs = extractor.getSampleTime();
                    }

                    //然后将当前ByteBuffer重新加入到队列中交给编解码器做下一步读取操作
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleDataSize, presentationTimeUs,
                            decodeInputEnd ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    //前进到下一段采样数据
                    if(!decodeInputEnd){
                        extractor.advance();
                    }
                } else {
                    Log.i(TAG,"inputBufferIndex < 0");
                }

                //操作解码输出数据
                //从队列中获取当前解码器处理输出数据的ByteBuffer序号
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeOutUs);

                if(outputBufferIndex < 0){
                    //输出ByteBuffer序号<0， 可能是输出缓存变化了， 输出格式信息变化了
                    switch (outputBufferIndex){
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            outputBuffers = mediaCodec.getOutputBuffers();
                            Log.e(TAG,"MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED [AudioDecoder]output buffers have changed.");
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            outputFormat = mediaCodec.getOutputFormat();

                            simpleRate = outputFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? outputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE) : simpleRate;
                            channelCount = outputFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? outputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : channelCount;
                            byteNumber =
                                    (outputFormat.containsKey("bit-width") ? outputFormat.getInteger("bit-width") : 0)
                                            / 8;
                            Log.i(TAG,"byteNumber :"+ byteNumber);
                            Log.e(TAG,
                                    "MediaCodec.INFO_OUTPUT_FORMAT_CHANGED [AudioDecoder]output format has changed to "
                                            + mediaCodec.getOutputFormat());
                            break;
                    }

                    continue;
                }

                //取得当前解码器处理输出数据的ByteBuffer
                targetBuffer = outputBuffers[outputBufferIndex];

                byte[] sourceByteArray = new byte[bufferInfo.size];

                //将解码的targetBuffer中的数据复制到sourceByteArray中
                targetBuffer.get(sourceByteArray);
                targetBuffer.clear();

                //释放当前的输出缓存
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

                //判断当前是否解码数据全部结束了
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    decodeOutputEnd = true;
                }


                //sourceByteArray就是最终解码后的采样数据
                //接下来可以对这些数据进行采样位数，声道的转换，但这是可选的，默认是和源音频一样的声道和采样位数
                if (sourceByteArray.length > 0 && bufferedOutputStream != null) {

                    //将解码后的PCM数据写入到PCM文件
                    try {
                        bufferedOutputStream.write(sourceByteArray);
                    } catch (Exception e) {
                        Log.e(TAG,"输出解压音频数据异常" + e);
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }


        if(bufferedOutputStream != null){
            try{
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        //释放mediaCodec 和 mediaExtractor
        if(mediaCodec != null){
            mediaCodec.stop();
            mediaCodec.release();
        }

        if(extractor != null){
            extractor.release();
        }
    }
}
