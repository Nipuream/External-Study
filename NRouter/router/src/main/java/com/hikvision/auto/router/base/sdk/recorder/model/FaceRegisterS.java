package com.hikvision.auto.router.base.sdk.recorder.model;

import com.hikvision.auto.router.base.sdk.comm.ProcRes;
import com.hikvision.auto.router.info.FaceInfo;

public class FaceRegisterS extends ProcRes {

    /**
     * 注册结果
     */
    private boolean isSuccess;

    /**
     * 注册的司机
     */
    private FaceInfo faceInfo;


    public FaceRegisterS(boolean isSuccess, FaceInfo faceInfo) {
        this.isSuccess = isSuccess;
        this.faceInfo = faceInfo;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    @Override
    public String toString() {
        return "FaceRegisterS{" +
                "isSuccess=" + isSuccess +
                ", faceInfo=" + faceInfo +
                '}';
    }
}
