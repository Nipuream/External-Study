package com.hikvision.auto.router.base.business.recorder.model;

import com.hikvision.auto.router.base.comm.BaseModelB;
import com.hikvision.auto.router.info.FileModel;
import java.util.List;

public class FileRetriveModelB extends BaseModelB {

    private List<FileModel> info;

    public FileRetriveModelB(List<FileModel> info) {
        this.info = info;
    }

    public List<FileModel> getInfo() {
        return info;
    }

    public void setInfo(List<FileModel> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "ImageRetriveModel{" +
                ", info=" + info +
                '}';
    }
}
