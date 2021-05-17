package com.hikvision.auto.router.base.sdk.peripheral;

import com.hikvision.auto.router.base.sdk.peripheral.callback.Passthrough;

public interface IPeripheral {

    /**
     * 注册透传回调
     * @param passthrough
     * @return
     */
    boolean attachPassThrougth(Passthrough passthrough);

    /**
     * 注销透传回调
     * @param passthrough
     * @return
     */
    boolean detachPassThrougth(Passthrough passthrough);

    /**
     * 发送透传数据
     * @param portNum
     * @param data
     * @return
     */
    boolean sendPassThrougthMsgData(String portNum, byte[] data);

}
