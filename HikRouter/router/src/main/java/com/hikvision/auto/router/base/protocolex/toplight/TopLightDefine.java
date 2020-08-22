package com.hikvision.auto.router.base.protocolex.toplight;

import com.hikvision.auto.apt_process.Describe;
import com.hikvision.auto.apt_process.TypeDefine;

public class TopLightDefine {

    @Describe(value = "获取顶灯上行数据接口",
             invoke = "com.hikvision.auto.router.base.protocolex.toplight.ITopLight.attachListener",
             path = ITopLight.PATH)
    public static class AttachListener {
        @TypeDefine(value = "com.hikvision.auto.router.base.protocolex.toplight.callback.ITopLightListener", define = "回调接口")
        public static String listener = "listener";
    }

}
