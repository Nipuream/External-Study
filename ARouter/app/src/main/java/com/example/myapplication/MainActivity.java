package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.myapplication.info.Test;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(getClass().getName(),"wait for flip test activity.");

        new Thread(){

            @Override
            public void run() {
                super.run();

                //1. 应用类 简单的跳转
//                ARouter.getInstance().build("/test/activity").navigation();

                //2. 携带参数跳转
                ARouter.getInstance().build("/test/activity")
                        .withLong("key1",666L)
                        .withString("key2","999")
                        .withObject("key3",new Test("Jack","Rose"))
                        //获取单次跳转结果
                        .navigation(MainActivity.this, new NavigationCallback() {
                            @Override
                            public void onFound(Postcard postcard) {

                            }

                            @Override
                            public void onLost(Postcard postcard) {

                            }

                            @Override
                            public void onArrival(Postcard postcard) {

                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {

                            }
                        });
            }
        }.start();

    }

}
