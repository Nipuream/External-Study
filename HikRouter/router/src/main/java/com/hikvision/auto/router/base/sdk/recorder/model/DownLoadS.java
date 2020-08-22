package com.hikvision.auto.router.base.sdk.recorder.model;

import com.hikvision.auto.router.base.sdk.comm.ProcRes;

public class DownLoadS extends ProcRes {

    //下载结果
    private boolean result;
    //下载路径
    private String path;

    public DownLoadS(boolean result, String path) {
        this.result = result;
        this.path = path;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "DowLoadS{" +
                "result=" + result +
                ", path='" + path + '\'' +
                '}';
    }

}
