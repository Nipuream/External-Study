package com.dahua.dhcontrolcenter.render;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * author  :  246747
 * date    :  2021/4/30
 * email   :  yang_hui4@dahuatech.com
 * describe:  网络键盘渲染模块，opengl +  surfacetexture 实现渲染和抓拍
 */
public class KeyBoardRender implements SurfaceTexture.OnFrameAvailableListener{

    private static final String TAG = KeyBoardRender.class.getName();
    private SurfaceTexture surfaceTexture;
    private Surface mSurface;
    // Native object address.
    private long renderHandle;
    // guards mFrameAvailable
    private final Object mFrameSyncObject = new Object();
    private boolean mFrameAvailable;
    private float[] stMatrix = new float[16];
    private ByteBuffer mPixelBuf;
    private int width, height;

    //初始化键盘渲染模块 output  显示的surface.
    private native boolean native_init(Surface output, int width, int height);
    //获取离线texture
    private native int GetOfflineTexture();
    //绘制surfacetexture.
    public native void drawImage(boolean invert);
    //更新纹理矩阵
    private native void updateMatrix(float[] stMatrix);
    //释放资源
    private native void native_release();
    //打开日志打印
    private native void open_debug(boolean debug);
    //抓拍
    public native void capture();

    public KeyBoardRender(Surface output, int width, int height){
        this.width = width;
        this.height = height;
        open_debug(false);
        native_init(output, width, height);
        setup();
        mPixelBuf = ByteBuffer.allocateDirect(width * height * 4);
        mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 创建surface surfacetexture.
     */
    private void setup(){
        int texture = GetOfflineTexture();
        Log.i(TAG,"texture : "+ texture);
        surfaceTexture = new SurfaceTexture(texture);
        surfaceTexture.setOnFrameAvailableListener(this);

        mSurface = new Surface(surfaceTexture);
    }

    public Surface getSurface(){
        return mSurface;
    }

    public void release(){
        native_release();

        //release surfacetexture.
        if(mSurface != null){
            mSurface.release();
        }
        mSurface = null;
        surfaceTexture = null;
    }

    public void awaitNewImage(){

        final int TIMEOUT_MS = 2000;
        synchronized (mFrameSyncObject){
            while (!mFrameAvailable){
                try {
                    mFrameSyncObject.wait(TIMEOUT_MS);
                    if(!mFrameAvailable){
                        Log.w(TAG,"frame wait time out.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mFrameAvailable = false;
        }

        //更新纹理
        surfaceTexture.updateTexImage();
    }

    /**
     * 更新 native层纹理矩阵
     */
    public void updateMatrix(){
        if(surfaceTexture != null)
            surfaceTexture.getTransformMatrix(stMatrix);

        updateMatrix(stMatrix);
    }

    /**
     * JNI 传入纹理像素
     * @param pixels
     */
    public void copyPixelsFromNative(byte[] pixels){

        mPixelBuf.rewind();
        mPixelBuf.put(pixels);

        File outputFile = new File(Environment.getExternalStorageDirectory(), "frame.png");
        BufferedOutputStream bos = null;
        try{

            bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mPixelBuf.rewind();
            bmp.copyPixelsFromBuffer(mPixelBuf);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bmp.recycle();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        Log.i(TAG,"new frame available.");
        synchronized (mFrameSyncObject){
            if(mFrameAvailable){
                Log.w(TAG,"mFrameAvailable already set, frame could be dropped");
            }
            mFrameAvailable = true;
            mFrameSyncObject.notifyAll();
        }
    }

    static {
        System.loadLibrary("render-lib");
    }

}
