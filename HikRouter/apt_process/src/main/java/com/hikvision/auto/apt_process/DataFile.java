package com.hikvision.auto.apt_process;

import java.util.Map;

public class DataFile {

    public  String uri;
    public  String tableName;
    public Map<String,String> fields;

    @Override
    public String toString() {
        return "DataFile{" +
                "uri='" + uri + '\'' +
                ", tableName='" + tableName + '\'' +
                ", fields=" + fields +
                '}';
    }
}
