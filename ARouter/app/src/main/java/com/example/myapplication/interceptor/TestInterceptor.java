package com.example.myapplication.interceptor;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;

@Interceptor(priority = 8, name = "testInterceptor")
public class TestInterceptor implements IInterceptor {

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {

        Log.i(getClass().getName(),"interceptor.");
        callback.onContinue(postcard); //处理完成，交还控制权
        //如果有问题，中断路由流程
//        callback.onInterrupt(new RuntimeException("exception occur."));

    }

    @Override
    public void init(Context context) {
        //拦截器的初始化，sdk初始化的时候调用此方法，仅会调用一次

    }
}
