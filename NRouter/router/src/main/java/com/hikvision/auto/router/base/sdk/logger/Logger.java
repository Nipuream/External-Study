package com.hikvision.auto.router.base.sdk.logger;

public interface Logger {

    /**
     * 日志是否起效
     *
     * @param debugger
     */
    void setDebugger(boolean debugger);

    /**
     * 打印
     *
     * @param msg
     */
    void d(String tag, String msg);

    void e(String tag, String msg);

    void i(String tag, String msg);

}
