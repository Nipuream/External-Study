package com.hikvision.auto.router.base.protocolex.taximeter;

import com.hikvision.auto.apt_process.Describe;
import com.hikvision.auto.apt_process.TypeDefine;
import com.hikvision.auto.router.base.protocolex.taximeter.callback.ITaxiMeterListener;

public class CallbackDefine {


    @Describe(value = "计价器心跳通知",
              invoke = "com.hikvision.auto.router.base.protocolex.taximeter.callback.ITaxiMeterListener.heartBeat",
              path = ITaxiMeterListener.PATH)
    public static class HeatBeat {

        @TypeDefine(value = "Integer", define = "计价器当前状态")
        public static String taximeterState = "taximeterState";
        @TypeDefine(value = "java.lang.String", define = "单位代码")
        public static String businessCode = "businessCode";
        @TypeDefine(value = "java.lang.String", define = "驾驶员证件号码")
        public static String certId = "certId";
        @TypeDefine(value = "java.lang.String", define = "车牌号")
        public static String carNumber = "carNumber";
        @TypeDefine(value = "java.lang.String", define = "当班累积里程")
        public static String businessMile = "businessMile";
        @TypeDefine(value = "java.lang.String", define = "当班累积行驶里程")
        public static String totalMile = "totalMile";
        @TypeDefine(value = "java.lang.String", define = "当班累积停驶里程")
        public static String totalStopMile = "totalStopMile";
        @TypeDefine(value = "Integer", define = "当前车次")
        public static String carNo = "carNo";
        @TypeDefine(value = "java.lang.String", define = "进入重车时间")
        public static String heavyCarTime = "heavyCarTime";
    }


}
