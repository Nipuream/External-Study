package com.hikvision.auto.router.base.business.recorder;

import com.hikvision.auto.apt_process.Describe;
import com.hikvision.auto.apt_process.TypeDefine;

/**
 * 行车记录仪 音视频相关的业务场景定义
 */
public class RecorderDefine {

    @Describe(value = "根据时间范围检索图片文件",
              invoke = "com.hikvision.auto.router.base.business.recorder.IRecorder.imageRetrive",
              path = IRecorder.PATH,
              returnType = "com.hikvision.auto.router.base.business.recorder.model.FileRetriveModelB.class")
    public static class ImageRetrive {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";  //(int)
        @TypeDefine(value = "Integer", define = "摄像头ID")
        public static String cameraId = "cameraId"; //(int)
        @TypeDefine(value = "Integer", define = "拍照原因")
        public static String reason = "reason"; //(int)
        @TypeDefine(value = "Long", define = "开始时间")
        public static String startTime = "startTime"; //(String)
        @TypeDefine(value = "Long", define = "结束时间")
        public static String endTime = "endTime"; //(String)
    }

    @Describe(value = "指定某一路摄像头进行拍照",
              invoke = "com.hikvision.auto.router.base.business.recorder.IRecorder.takePhoto",
              path = IRecorder.PATH,
              returnType = "com.hikvision.auto.router.base.business.recorder.model.TakePhotoModelB.class")
    public static class TakePhoto {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "Integer", define = "摄像头Id")
        public static String cameraId = "cameraId";
        @TypeDefine(value = "Integer", define = "拍照间隔时间")
        public static String interval = "interval";
        @TypeDefine(value = "Integer", define = "拍照数量")
        public static String numbers = "numbers";
    }

    @Describe(value = "从联永端下载文件",
              invoke = "com.hikvision.auto.router.base.business.recorder.IRecorder.downloadFile",
              path = IRecorder.PATH,
              returnType = "com.hikvision.auto.router.base.business.recorder.model.DownLoadModelB.class")
    public static class DownloadFile {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "java.lang.String", define = "联永端路径名", repeat = "urls")
        public static String url = "url";
    }

    @Describe(value = "司机信息注册，包括本地注册和联永端注册",
              invoke = "com.hikvision.auto.router.base.business.recorder.IRecorder.driverRegister",
              path = IRecorder.PATH,
              returnType = "com.hikvision.auto.router.base.business.recorder.model.FaceRegisterModelB.class")
    public static class FaceRegister {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "Integer", define = "驾驶员唯一标识，一般是驾驶员行驶证号", repeat = "faceInfo")
        public static String driverIndexCode = "driverIndexCode";
        @TypeDefine(value = "java.lang.String", define = "司机姓名")
        public static String name = "name";
        @TypeDefine(value = "Integer", define = "性别")
        public static String sex = "sex";
        @TypeDefine(value = "Integer", define = "证件类型，0-运营资格证 1-身份证。默认为运营资格证号", repeat = "faceInfo")
        public static String cardType = "cardType";
        @TypeDefine(value = "java.lang.String", define = "证件ID，运营资格证号或身份证号", repeat = "faceInfo")
        public static String cardID = "cardID";
        @TypeDefine(value = "java.lang.String", define = "驾驶员图片下载中心", repeat = "faceInfo")
        public static String photoUrl = "photoUrl";
        @TypeDefine(value = "java.lang.String", define = "驾驶员最新更变时间", repeat = "faceInfo")
        public static String lastTime = "lastTime";
        @TypeDefine(value = "Integer", define = "驾驶员信息版本", repeat = "faceInfo")
        public static String driverVersion = "driverVersion";
        @TypeDefine(value = "java.lang.String", define = "请求网址", repeat = "faceInfo")
        public static String netUrl = "netUrl";
    }

}
