package com.hikvision.auto.router.base.platform;

/**
 * 平台通信，包括和平台之间通信、双平台连接
 * 消息组装、转义、发送、接收功能
 */
public interface IPlatformComm {

    /**
     * 和平台连接初始化
     * 开始连接平台
     */
    public void initPlatform();

    /**
     * 获取流水号
     * @return
     */
    public int getSerial();

    /**
     * 向平台请求发送数据
     * @param msgId
     * @param body
     */
    public void request(int msgId , byte[] body);

    /**
     * attach platform listener.
     * @param listener
     */
    public void attachListener(IPlatformListener listener);

    /**
     * detach platform listener.
     * @param listener
     */
    public void detachListener(IPlatformListener listener);


    public interface IPlatformListener {
        /**
         * 平台回调的消息体
         * @param body
         */
        void response(byte[] body);
    }

}
