package com.hikvision.auto.router.base.protocolex.taximeter;

import com.hikvision.auto.router.base.comm.BaseModelB;
import com.hikvision.auto.router.base.comm.BaseCalB;
import com.hikvision.auto.router.base.protocolex.taximeter.callback.ITaxiMeterListener;

public interface ITaxiMeter {

    String PATH = "protocolex/taximeter";

    /**
     * 获取计价器上行数据接口
     * @param listener
     */
    void attachListener(ITaxiMeterListener listener);

    /**
     * 查询计价器状态
     */
    void queryMeterState(String json);

}
