package com.hikvision.auto.router;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RouterApp extends Application {

    static RouterApp instance;
    private ThreadPoolExecutor executor ;
    //Don't modify it field name.
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        //todo 初始化工作
        instance = this;
        executor = new ThreadPoolExecutor(2,10,3,
                TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
    }

    public static RouterApp getRouterApp(){
        return  instance;
    }

    public ThreadPoolExecutor executor(){
        return executor;
    }

}
