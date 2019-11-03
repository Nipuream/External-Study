package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "app_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_activity_main);

        Log.d(getClass().getName(),"wait for flip test activity.");

        new Thread(){

            @Override
            public void run() {
                super.run();

                //1. 应用类 简单的跳转
                ARouter.getInstance().build("/work/main").navigation(MainActivity.this, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        Log.d(TAG,"onFound : "+postcard.toString());
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        Log.d(TAG,"onLost : "+postcard.toString());
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        Log.d(TAG,"onArrival : "+postcard.toString());
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        Log.d(TAG,"onInterrupt : "+postcard.toString());
                    }
                });

      /*          //2. 携带参数跳转
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
                        });*/
            }
        }.start();

    }

}
