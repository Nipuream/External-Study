package com.hikvision.auto.router.base.protocolex.taximeter.callback;

/**
 * 数据上行
 */
public interface ITaxiMeterListener {

    String PATH = "protocolex/taximetercallback";

    /**
     * 心跳
     * @param json
     */
    void heartBeat(String json);

}
