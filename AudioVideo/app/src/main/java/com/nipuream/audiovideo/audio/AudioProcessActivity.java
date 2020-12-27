package com.nipuream.audiovideo.audio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nipuream.audiovideo.NativeLib;
import com.nipuream.audiovideo.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.security.auth.login.LoginException;


public class AudioProcessActivity extends AppCompatActivity {

    private static final String TAG = "AudioProcessActivity";

    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050 16000 11025
    //采样频率一般分为 22.05khz 44.1khz 48khz
    private final static int AUDIO_SAMPLE_RATE = 44100;
    //声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    //缓冲区字节大小，这个一般由硬件决定的
    private int bufferSizeInBytes = 0;
    //录音对象
    private AudioRecord audioRecord;
    //录音文件
    private File file = null;
    //是否暂停
    private boolean pause ;
    //是否已经开始录音
    private boolean start = false;

    private boolean audioIsPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_process);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"已经申请过权限了");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }

        //获取缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        Log.i(TAG,"bufferSizeInBytes : "+ bufferSizeInBytes);

        //创建默认的录音对象
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);

        //创建文件
        File dir = new File("sdcard/Android");
        if(!dir.exists()){
            Log.i(TAG, "dir is not exits.");
            return ;
        }

        file = new File(dir, "yanghui.pcm");
    }


    public void startRecord(View view) {

        if(start){
            Log.i(TAG,"record is starting...");
            return ;
        }

        start = true;
        pause = false;
        audioRecord.startRecording();

        new Thread(){

            @Override
            public void run() {
                super.run();

                byte[] data = new byte[bufferSizeInBytes];
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(file);
                    while (!pause){

                        if(audioRecord.read(data, 0 , bufferSizeInBytes) > 0) {
                            fos.write(data);
                        } else {
                            Log.i(TAG,"audio record error !!!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if(fos != null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }.start();
    }


    public void stopRecord(View view) {

        Log.i(TAG,"stop record...");
        pause = true;
        audioRecord.stop();
        start = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG,"requestCode : "+ requestCode + ", permisssion : "+ Arrays.toString(permissions)
                + ", grantResults : "+ Arrays.toString(grantResults));
    }

    public void encodeAAC(View view) {

        new Thread(){

            @Override
            public void run() {
                super.run();
                int bitRates = AUDIO_SAMPLE_RATE * 16 ;
                NativeLib.encodeAAC("sdcard/Android/yanghui.pcm",
                        1,bitRates,AUDIO_SAMPLE_RATE,"sdcard/Android/output.aac");
                Log.i(TAG,"encode AAC end.");
            }
        }.start();
    }


    public void playPcm(View view) {

        if(audioIsPlay){
            Log.i(TAG,"audio is playing...");
            return ;
        }

        audioIsPlay = true;
        new Thread(){

            @Override
            public void run() {
                super.run();

                int bufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
                FileInputStream fis = null;
                try {

                    fis = new FileInputStream("/sdcard/Android/yanghui.pcm");
                    audioTrack.play();

                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    while ((len = fis.read(buffer)) != -1){
                        audioTrack.write(buffer, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(fis != null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(audioTrack != null){
                        audioTrack.stop();
                        audioTrack = null;
                    }
                    audioIsPlay = false;
                    Log.i(TAG,"audio play end ...");
                }
            }

        }.start();

    }

}