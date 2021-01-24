package com.nipuream.audiovideo;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioVideo extends Application {

    public static ExecutorService execPool;

    @Override
    public void onCreate() {
        super.onCreate();
        execPool = Executors.newFixedThreadPool(2);
    }


}
