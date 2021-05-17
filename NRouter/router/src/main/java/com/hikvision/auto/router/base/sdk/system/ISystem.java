package com.hikvision.auto.router.base.sdk.system;

import com.hikvision.auto.router.base.sdk.system.type.CallType;
import com.hikvision.auto.router.info.Position;


public interface ISystem {

    /**
     * 获取位置
     * @return
     */
    Position currentPos();

    /**
     * 获取车辆acc状态
     * @return
     */
    boolean getAccState();

    /**
     * 播放对应tts文本
     * @param info
     */
    void playTtsInfo(String info);

    /**
     * 拨打电话
     * @param phoneNumber
     * @param type @see {@link CallType}
     */
    void startCall(String phoneNumber,@CallType int type);
}
