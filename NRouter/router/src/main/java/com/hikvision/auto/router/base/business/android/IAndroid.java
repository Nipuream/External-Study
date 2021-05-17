package com.hikvision.auto.router.base.business.android;

public interface IAndroid {

    String PATH = "business/android";

    /**
     * 电话回拨
     * @param json
     * @return
     */
    boolean phoneCall(String json);

    /**
     * 设置电话本
     * @param json
     * @return
     */
    boolean settingPhoneBook(String json);


}
