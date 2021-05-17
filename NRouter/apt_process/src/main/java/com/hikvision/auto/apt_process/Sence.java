package com.hikvision.auto.apt_process;

import java.util.HashMap;
import java.util.Map;

public class Sence {

    //描述
    public String describe;
    //调用方法
    public String invoker_method;
    //返回类型
    public String returnType;
    //字段描述
    public Map<String,String> fileds = new HashMap<>();
    //array.
    public Map<String, Map<String,String>> arrays = new HashMap<>();
    //路由文件
    public String routerFile;
    //保存文件夹
    public String dir;

    @Override
    public String toString() {
        return "Sence{" +
                "describe='" + describe + '\'' +
                ", invoker_method='" + invoker_method + '\'' +
                ", returnType='" + returnType + '\'' +
                ", fileds=" + fileds +
                ", arrays=" + arrays +
                ", routerFile='" + routerFile + '\'' +
                ", dir='" + dir + '\'' +
                '}';
    }
}
