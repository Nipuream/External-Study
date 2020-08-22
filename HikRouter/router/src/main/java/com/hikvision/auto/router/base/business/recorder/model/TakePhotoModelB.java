package com.hikvision.auto.router.base.business.recorder.model;

import com.hikvision.auto.router.base.comm.BaseModelB;
import java.util.List;

/**
 * 拍照返回结果
 * paths 指的是存在高通这边的图片路径
 */
public class TakePhotoModelB extends BaseModelB {

    private List<String> paths;

    public TakePhotoModelB(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "TakePhotoModelB{" +
                "paths=" + paths +
                '}';
    }
}
