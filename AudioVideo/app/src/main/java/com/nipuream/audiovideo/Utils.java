package com.nipuream.audiovideo;

import android.hardware.Camera;

import java.util.List;

public class Utils {

    public static boolean isSupportPreviewSize(List<Camera.Size> supportSize, int previewWidth, int previewHeight){

        for(Camera.Size size : supportSize){
            if(size.width == previewWidth && size.height == previewHeight){

                return true;
            }
        }
        return false;
    }


}
