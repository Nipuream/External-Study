package com.hikvision.auto.router.base.data;

import com.hikvision.auto.apt_process.DataSource;

public class DataDefine {

    @DataSource(uri = "content://com.hikvision.auto.dataprovider/position", table = "position")
    public static class Position {
        @DataSource(type = "REAL")
        public static String ACCURACY = "ACCURACY";
        @DataSource(type = "REAL")
        public static String LONGITUDE = "LONGITUDE";
        @DataSource(type = "REAL")
        public static String LATITUDE = "LATITUDE";
        @DataSource(type = "INTEGER")
        public static String TIME = "TIME";
        @DataSource(type = "REAL")
        public static String ALTITUDE = "ALTITUDE";
        @DataSource(type = "REAL")
        public static String SPEED = "SPEED";
        @DataSource(type = "REAL")
        public static String BEARING = "BEARING";
        @DataSource(type = "INTEGER")
        public static String SATELLITES = "SATELLITES";
    }

    @DataSource(uri = "content://com.hikvision.auto.dataprovider/contact", table = "contract")
    public static class Contact {
        @DataSource(type = "INTEGER")
        public static String FLAG = "FLAG";
        @DataSource(type = "TEXT")
        public static String PHONE_NUMBER = "PHONE_NUMBER";
        @DataSource(type = "TEXT")
        public static String CONTRACT = "CONTRACT";
    }


    @DataSource(uri = "content://com.hikvision.auto.dataprovider/driverInfo", table = "driverInfo")
    public static class DriverInfo {
        @DataSource(type = "INTEGER")
        public static String _id = "_id";
        @DataSource(type = "TEXT")
        public static String DRIVER_INDEX_CODE = "DRIVER_INDEX_CODE";
        @DataSource(type = "TEXT")
        public static String NAME = "NAME";
        @DataSource(type = "INTEGER")
        public static String SEX = "SEX";
        @DataSource(type = "INTEGER")
        public static String CARD_TYPE = "CARD_TYPE";
        @DataSource(type = "TEXT")
        public static String CARD_ID = "CARD_ID";
        @DataSource(type = "TEXT")
        public static String PHOTO_URL = "PHOTO_URL";
        @DataSource(type = "TEXT")
        public static String LAST_TIME = "LAST_TIME";
        @DataSource(type = "INTEGER")
        public static String DRIVER_VERSION = "DRIVER_VERSION";
        @DataSource(type = "TEXT")
        public static String NET_URL = "NET_URL";
    }


}
