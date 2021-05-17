package com.hikvision.auto.router.base.comm;

import com.hikvision.auto.router.base.comm.BaseModelB;

public interface BaseCalB<T extends BaseModelB> {

    /**
     * 业务组件处理完结果交由调用者
     * @param result
     */
    void response(String result);

}
