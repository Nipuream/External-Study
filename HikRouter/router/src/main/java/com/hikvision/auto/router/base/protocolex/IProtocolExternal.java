package com.hikvision.auto.router.base.protocolex;

import com.hikvision.auto.router.base.protocolex.comm.ExternalProtocol;
import com.hikvision.auto.router.base.protocolex.taximeter.ITaxiMeter;
import com.hikvision.auto.router.base.protocolex.toplight.ITopLight;

public interface IProtocolExternal {

    /**
     * 选择地级市协议
     * @param protocol
     */
    void defineProtocol(@ExternalProtocol int protocol);

    /**
     * 获取计价器接口
     * @return
     */
    ITaxiMeter taximeter();

    /**
     * 获取顶灯接口
     * @return
     */
    ITopLight topLight();

}
