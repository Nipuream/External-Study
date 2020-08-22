package com.hikvision.auto.apt_process;

import java.util.Map;

public class RouterFile {

    public String routerFile;
    public Map<String,Sence> senceMap;

    @Override
    public String toString() {
        return "RouterFile{" +
                "routerFile='" + routerFile + '\'' +
                ", senceMap=" + senceMap +
                '}';
    }
}
