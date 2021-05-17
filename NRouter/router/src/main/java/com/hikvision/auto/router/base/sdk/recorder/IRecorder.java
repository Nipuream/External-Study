package com.hikvision.auto.router.base.sdk.recorder;

import com.hikvision.auto.router.base.sdk.recorder.model.DownLoadS;
import com.hikvision.auto.router.base.sdk.recorder.model.FaceRegisterS;
import com.hikvision.auto.router.base.sdk.recorder.model.FileModelS;
import com.hikvision.auto.router.base.sdk.recorder.type.MediaType;
import com.hikvision.auto.router.base.sdk.comm.BaseCal;
import com.hikvision.auto.router.info.FaceInfo;

public interface IRecorder{

    /**
     *
     * 查询媒体文件列表
     *
     * @param index 序列
     * @param mediaType 查询的媒体文件类型，默认是视频
     * @param eventType 查询的事件类型 @see {@link com.hikvision.auto.router.base.sdk.recorder.type.MediaType}
     * @param chanNo 通道号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param cal 数据的回调
     * @return
     */
    void queryMediaFileList(int index, @MediaType int mediaType, int eventType, int chanNo, long startTime,
                            long endTime, BaseCal<FileModelS> cal);


    /**
     * 抓拍
     * @param chanNo
     * @param type @see {@link com.hikvision.auto.router.base.sdk.recorder.type.EventType}
     * @param cal
     */
    void takePhoto(int chanNo, int type, BaseCal<FileModelS> cal);


    /**
     * 从联永下载文件
     * @param url  联永url
     * @param path 高通本地文件夹
     * @param name 高通本地文件
     */
    void dowloadFile(String url, String path, String name, BaseCal<DownLoadS> cal);


    /**
     * 向联永端注册人脸
     * @param info
     * @param cal
     */
    void faceRegister(FaceInfo info, BaseCal<FaceRegisterS> cal);


}
