package com.hikvision.auto.router.base.sdk.recorder.model;

import java.util.List;
import com.hikvision.auto.router.info.FileModel;
import com.hikvision.auto.router.base.sdk.comm.ProcRes;

public class FileModelS extends ProcRes {

    private int index;
    private int totalNum;
    private List<FileModel> infos;

    public FileModelS(int index, int totalNum, List<FileModel> infos) {
        this.index = index;
        this.totalNum = totalNum;
        this.infos = infos;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<FileModel> getInfos() {
        return infos;
    }

    public void setInfos(List<FileModel> infos) {
        this.infos = infos;
    }

    @Override
    public String toString() {
        return "FileModelCal{" +
                "index=" + index +
                ", totalNum=" + totalNum +
                ", infos=" + infos +
                '}';
    }
}
