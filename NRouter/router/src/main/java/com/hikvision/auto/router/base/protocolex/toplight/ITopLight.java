package com.hikvision.auto.router.base.protocolex.toplight;

import com.hikvision.auto.router.base.protocolex.toplight.callback.ITopLightListener;

public interface ITopLight {

    String PATH = "protocolex/toplight";

    /**
     * 获取上行数据接口
     * @param listener
     */
    void attachListener(ITopLightListener listener);


}
