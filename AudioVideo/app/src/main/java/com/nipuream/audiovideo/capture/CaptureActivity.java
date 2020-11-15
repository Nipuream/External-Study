package com.nipuream.audiovideo.capture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.nipuream.audiovideo.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 * 采集音视频
 */
public class CaptureActivity extends AppCompatActivity {

    CameraManager mananger;
    private Handler mainHandler = null;
    private HandlerThread handlerThread = null;
    private Handler clientHandler = null;
    private CameraDevice mCameraDevice;
    SurfaceHolder surfaceHolder = null;
    SurfaceView surfaceView ;
    ImageReader mImageReader;
    FileOutputStream fileOutputStream = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        mainHandler = new Handler(getMainLooper());
        surfaceView = findViewById(R.id.capture_surfaceView);
        surfaceView.getHolder().addCallback(new CaptureActivity.SurfaceCallback());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i("yanghui", "已经申请过了动态权限");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }



    private void setupCamera2(final int width, final int height) {

        handlerThread = new HandlerThread("camera2");
        handlerThread.start();
        clientHandler = new Handler(handlerThread.getLooper());

        String cameraId = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);
        mananger = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mImageReader = ImageReader.newInstance(width,height, ImageFormat.YUV_420_888, 2);
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {

                    //获取到照片数据
                    Image image = imageReader.acquireLatestImage();

                    if(image == null){
                        return ;
                    }


                    Image.Plane[] planes = image.getPlanes();

                    //plane.pixelStride : 1, rowStride : 1472, width : 1440, height : 1080, buffer.size : 1589728
                    //plane.pixelStride : 2, rowStride : 1472, width : 1440, height : 1080, buffer.size : 794847
                    //plane.pixelStride : 2, rowStride : 1472, width : 1440, height : 1080, buffer.size : 794847
//                    for(Image.Plane plane : planes){
//                        Log.i("yanghui","plane.pixelStride : "+ plane.getPixelStride() + ", rowStride : " + plane.getRowStride() + ", width : "+ image.getWidth() + ", height : "+ image.getHeight() + ", buffer.size : " + plane.getBuffer().remaining());
//                    }


                    byte[] i420bytes = CameraUtil.getDataFromImage(image, CameraUtil.COLOR_FormatI420);
//                    byte[] i420RotateBytes = ImageUtil.rotateYUV420Degree90(i420bytes,width, height);
                    if(fileOutputStream != null){
                        try {
                            fileOutputStream.write(i420bytes,0, i420bytes.length);
                            Log.i("yanghui","write to file size : "+ i420bytes.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.close();
                }
            },mainHandler);

            //打开摄像头
            mananger.openCamera(cameraId, stateCallback, mainHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            //开启预览
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

            if(null != mCameraDevice){
                mCameraDevice.close();
                mCameraDevice = null;
            }

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Log.i("yanghui","摄像头开启失败");
        }
    };


    public void preview2(View view) {

        try {
            File dir = getExternalCacheDir();
            Log.i("yanghui","dir : "+ dir.toString());
            File file = new File(dir, "yanghui.yuv");
            fileOutputStream = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            previewRequestBuilder.addTarget(mImageReader.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                    if(null == mCameraDevice){
                        return ;
                    }

                    try {
                        //自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        //显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(previewRequest, null,clientHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.i("yanghui","配置失败");

                }
            }, clientHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 在5.0 以上手机已经废弃了 之前api.
     * @param view
     */
    public void preview1(View view) {
    }

    public void stopCapture(View view) {

        if(fileOutputStream != null){
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileOutputStream = null;
        }


    }


    private final class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            CaptureActivity.this.surfaceHolder = surfaceHolder;

//            setSurface(surfaceHolder.getSurface());
//            showBitmap();


            if (ActivityCompat.checkSelfPermission(CaptureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            setupCamera2(1080,1440);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }


}