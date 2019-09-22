package com.example.basemodule.service;


import com.alibaba.android.arouter.facade.template.IProvider;


/**
 * 申明接口，其他组件通过接口来调用服务
 */
public interface HelloService extends IProvider {

    String sayHello(String name);


}
