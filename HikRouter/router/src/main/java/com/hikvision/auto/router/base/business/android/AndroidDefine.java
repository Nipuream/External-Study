package com.hikvision.auto.router.base.business.android;

import com.hikvision.auto.apt_process.Describe;
import com.hikvision.auto.apt_process.TypeDefine;

public class AndroidDefine {

    @Describe(value = "电话回拨",
            invoke = "com.hikvision.auto.router.base.business.platform.IAndroid.phoneCall",
            path = IAndroid.PATH)
    public static class PhoneCall {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "Integer", define = "标志位，0:普通电话，1:监听")
        public static String flag = "flag";
        @TypeDefine(value = "java.lang.String", define = "电话号码")
        public static String phoneNumber = "phoneNumber";
    }

    @Describe(value = "设置电话本",
            invoke = "com.hikvision.auto.router.base.business.platform.IAndroid.settingPhoneBook",
            path = IAndroid.PATH)
    public static class SettingPhoneBook {
        @TypeDefine(value = "Integer", define = "流水号")
        public static String serial = "serial";
        @TypeDefine(value = "Integer", define = "联系人总数")
        public static String peopleNum = "peopleNum";
        @TypeDefine(value = "Integer", define = "标志，1:呼入；2:呼出;3：呼入/呼出", repeat = "people_array")
        public static String flag = "flag";
        @TypeDefine(value = "java/lang/String", define = "电话号码", repeat = "people_array")
        public static String phoneNumber = "phoneNumber";
        @TypeDefine(value = "java/lang/String", define = "联系人", repeat = "people_array")
        public static String peopleName = "peopleName";
    }


}
