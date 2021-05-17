package com.hikvision.auto.router.base.business.recorder;

import com.hikvision.auto.router.base.comm.BaseCalB;
import com.hikvision.auto.router.base.business.recorder.model.DownLoadModelB;
import com.hikvision.auto.router.base.business.recorder.model.FaceRegisterModelB;
import com.hikvision.auto.router.base.business.recorder.model.FileRetriveModelB;
import com.hikvision.auto.router.base.business.recorder.model.TakePhotoModelB;

/**
 * 行车记录仪相关处理的业务接口
 */
public interface IRecorder {

    /**
     * 生成对应路由表位置，也可根据此路径 HikRouter 自动给接口注入实例
     */
    String PATH = "business/recorder";

    /**
     * 存储图片检索
     * @param json
     * @param cal 图片检索回调
     */
    void imageRetrive(String json, BaseCalB<FileRetriveModelB> cal);

    /**
     * 拍照
     * @param json
     * @param cal
     */
    void takePhoto(String json, BaseCalB<TakePhotoModelB> cal);

    /**
     * 下载文件
     * @param json
     * @param cal
     */
    void downloadFile(String json, BaseCalB<DownLoadModelB> cal);

    /**
     * 司机注册
     * @param json
     */
    void driverRegister(String json, BaseCalB<FaceRegisterModelB> cal);

}
