package com.hikvision.auto.router.base.sdk.peripheral.callback;

public interface Passthrough {

    /**
     * 接收到透传数据
     * @param portNum
     * @param data
     */
    void onPassthroughResponse(String portNum, byte[] data);

    /**
     * 设置串口号和波特率回调
     * @param port
     * @param result
     */
    void onSetSrialPortBandResult(String port, int result);

}
