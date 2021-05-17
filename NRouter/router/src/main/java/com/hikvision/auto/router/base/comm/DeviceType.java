package com.hikvision.auto.router.base.comm;

public @interface DeviceType {

    /**
     * 智能服务终端（含主机、屏及智能视频组件等）
     */
    int ISU = 0x00;

    /**
     * 计价器
     */
    int TAXI_METER = 0x02;

    /**
     * 通讯安全控制模块
     */
    int SAFE_CONTROL = 0x03;

    /**
     * LED 广告显示屏
     */
    int LED_LIGHT = 0x04;

    /**
     * 智能顶灯
     */
    int TOP_LIGHT = 0x05;

    /**
     * 后排液晶 多媒体广告屏
     */
    int MEDIA_LIGHT = 0x09;

}
