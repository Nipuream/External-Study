package com.nipuream.audiovideo;

public class NativeLib {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();


    //使用 opengl es + surface 实现播放yuv数据
    public static native void playYUV(String path);

    //对画面进行旋转
    public static native  void rotation(byte[] data, int width,int height, int degress);

    //对画面进行缩放
    public static native void scale(byte[] src, byte[] dest, int srcWidth, int srcHeight,int dstWidth, int dstHeight);
}
