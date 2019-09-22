package com.example.myapplication.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.basemodule.service.HelloService;


@Route(path = "test/server/hello", name = "hello")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "hello , "+ name;
    }

    @Override
    public void init(Context context) {

    }
}
