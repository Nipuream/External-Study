package com.hikvision.auto.router.base.business.recorder.model;

import com.hikvision.auto.router.base.comm.BaseModelB;
import java.util.List;

/**
 * 下载文件返回结果
 * paths -> 指的是高通这边保存的路径
 */
public class DownLoadModelB extends BaseModelB {

    private List<String> paths;

    public DownLoadModelB(List<String> paths) {
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
        return "DownLoadModelB{" +
                "paths=" + paths +
                '}';
    }
}
