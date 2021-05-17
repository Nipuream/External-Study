package com.hikvision.auto.router.base.sdk.comm;

public interface BaseCal<T extends ProcRes> {

    /**
     * 业务处理层回调
     * @param t
     */
    void call(T t);

    /**
     * 错误码 @see {@link com.hikvision.auto.router.base.sdk.comm.Code}
     * @param code
     */
    void error(int code);

}
