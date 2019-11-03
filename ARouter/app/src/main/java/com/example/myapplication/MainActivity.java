package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;


/**
 * a.ARouter跳转的过程？
 *
 * 1.预处理url，PathReplaceService 实现路径动态加载
 * 2.Warehouse.reutes 匹配
 * 3.找到Url对应界面，如果找到则进行调转，没找到则进入第四步骤
 * 4.进行降级处理
 * 5.如果不是Activity则直接通过反射 返回对象的实例
 * 6.如果是Activity则进行拦截器处理，线程池 + CoutDownLauch
 *
 * b.如果将对象注入到带有Autowired的字段中去？
 * 1.ARouter.getInstance().inject(this);
 * 2.将每个this生成 ISyringe 类型的类
 * 3.将bundle中的值赋值到实例中的参数中去
 *
 * c.为什么通信不使用 startActivity?
 * 解耦合，可通过模块去查找对应的路由表，知道跳转到具体的Activity.
 *
 */
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
