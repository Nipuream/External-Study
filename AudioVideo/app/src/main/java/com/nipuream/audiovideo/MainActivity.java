package com.nipuream.audiovideo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.nipuream.audiovideo.audio.AudioProcessActivity;
import com.nipuream.audiovideo.capture.CaptureActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG = "MainActivity";

    SurfaceView surfaceView;
    Bitmap bitmap;
    SurfaceHolder surfaceHolder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Log.i("yanghui", "bitmap size : " + bitmap.getByteCount());
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }

    public void flipCapture(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    public void playYUVwithGl(View view) {

        File dir = getExternalCacheDir();
        Log.i("yanghui","dir : "+ dir.toString());
        File file = new File(dir, "yanghui.yuv");

        if(!file.exists()){
            Log.i(TAG,"file is not exits.");
            return ;
        } else {
            Log.i(TAG,"file path : "+ file.getPath() + ", start play yuv.");
        }

        NativeLib.playYUV(file.getPath());
    }

    public void recordAudio(View view) {
        startActivity(new Intent(this, AudioProcessActivity.class));
    }

    private final class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            MainActivity.this.surfaceHolder = surfaceHolder;
            setSurface(surfaceHolder.getSurface());
//            showBitmap();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }


    //将surface传递到native层
    public native void setSurface(Object surface);

    //使用native window 显示bitmap
    public native void showBitmap();


}
