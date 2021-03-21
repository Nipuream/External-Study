package com.nipuream.audiovideo.video;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "PreviewView";

    public PreviewView(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Surface surface = holder.getSurface();
        int width = getWidth();
        int height = getHeight();
        if(null != mCallback){
            mCallback.createSurface(surface, width, height);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(null != mCallback){
            mCallback.resetRenderSize(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(null != mCallback){
            mCallback.destroySurface();
        }
    }

    private PreviewCallback mCallback;
    public void setCallback(PreviewCallback mCallback){
        this.mCallback = mCallback;
    }
    public interface PreviewCallback  {
        public void createSurface(Surface surface, int width, int height);
        public void resetRenderSize(int width, int height);
        public void destroySurface();
    }

}
