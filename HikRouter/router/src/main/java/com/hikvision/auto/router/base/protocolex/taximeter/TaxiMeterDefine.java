package com.hikvision.auto.router.base.protocolex.taximeter;

import com.hikvision.auto.apt_process.Describe;
import com.hikvision.auto.apt_process.TypeDefine;

public class TaxiMeterDefine {


    @Describe(value = "注册计价器回调接口，返回数据均是计价器上行数据",
              invoke = "com.hikvision.auto.router.base.protocolex.taximeter.ITaxiMeter.attachListener",
              path = ITaxiMeter.PATH)
    public static class AttachListener {

        @TypeDefine(value = "com.hikvision.auto.router.base.protocolex.taximeter.callback.ITaxiMeterListener", define = "计价器数据上行回调接口")
        public static String listener = "listener";
    }

    @Describe(value = "计价器设备运行状态查询指令",
              invoke = "com.hikvision.auto.router.base.protocolex.taximeter.ITaxiMeter.queryMeterState",
              path = ITaxiMeter.PATH,
              returnType = "com.hikvision.auto.router.base.protocolex.taximeter.model.TaxiMeterStateQueryModel.class")
    public static class TaxiMeterQueryState {

        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "java.lang.String", define = "ISU当前时间")
        public static String currentTime = "currentTime";
    }

}
