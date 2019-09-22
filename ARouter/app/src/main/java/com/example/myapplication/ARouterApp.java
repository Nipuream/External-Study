package com.example.myapplication;

import android.app.Application;
import com.alibaba.android.arouter.launcher.ARouter;

public class ARouterApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ARouter.openLog();
        ARouter.openDebug();

        ARouter.init(this);

    }
}
