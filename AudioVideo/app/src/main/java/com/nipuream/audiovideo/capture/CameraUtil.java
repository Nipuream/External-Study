package com.nipuream.audiovideo.capture;

import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;

import com.nipuream.audiovideo.NativeLib;

import java.nio.ByteBuffer;

public class CameraUtil {

    private static final String TAG = "CameraUtil";

    /**
     * 将 camera2 生成的image 文件转化为yuv 数据  I420格式 yyyyuuvv
     * @param image
     * @return
     */
    public static byte[] getBytes_final (Image image){

        int format = image.getFormat();
        Log.i(TAG,"format : "+ format);

        if(format != ImageFormat.YUV_420_888){
            throw new IllegalStateException("Image format not is YUV_420_888 格式");
        }


        byte[] i420 = new byte[image.getWidth() * image.getHeight() * 3 / 2];
        int offset = 0;
        Image.Plane[] planes = image.getPlanes();

        //处理 Y
        int pixelStride = planes[0].getPixelStride(); // pixelStride == 1
        ByteBuffer yBuffer = planes[0].getBuffer();
        int rowStride = planes[0].getRowStride();

        byte[] skipRow = new byte[rowStride - image.getWidth()];
        byte[] row = new byte[image.getWidth()];

        for(int i = 0; i < image.getHeight(); i++){
            yBuffer.get(row);
            System.arraycopy(row,0,i420,offset,row.length);
            offset += row.length;

            if(i < image.getHeight() - 1){
                yBuffer.get(skipRow); //移除4字节对齐的 无效字节，最后一行不需要移除，因为后面接的是 UV 数据
            }
        }

        //处理UV
        byte[] u = processUVPlanes(planes[1], image.getWidth(), image.getHeight());
        System.arraycopy(u,0,i420,offset,u.length);
        offset += u.length;

        byte[] v = processUVPlanes(planes[2], image.getWidth(), image.getHeight());
        System.arraycopy(v, 0, i420, offset, v.length);

        NativeLib.rotation(i420, image.getWidth(), image.getHeight(), 270);
        return i420;
    }

    private static byte[] processUVPlanes(Image.Plane plane, int width, int height){


        byte[] last = new byte[width/2  * height / 2];
        int index = 0;

        int pixelStride = plane.getPixelStride();
        ByteBuffer uvBuffer = plane.getBuffer();
        int rowStride = plane.getRowStride();

        byte[] skipRowUv = new byte[rowStride - width];
        byte[] rowUv = new byte[width];
        for(int i = 0; i < height /2; i++){

//            Log.i(TAG,"remain size : " + uvBuffer.remaining() + ", i : "+ i);

            int length = width;
            if(uvBuffer.remaining() < length){
                length = uvBuffer.remaining();
            }

            uvBuffer.get(rowUv,0, length);
            for(int j = 0; j < length; j+=pixelStride){
                last[index++] = rowUv[j];
            }

            if(i < height/2 -1){
                uvBuffer.get(skipRowUv);
            }
        }

        return last;
    }






}
