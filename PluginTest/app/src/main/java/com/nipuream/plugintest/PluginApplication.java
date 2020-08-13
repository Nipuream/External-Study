package com.nipuream.plugintest;

import android.app.Application;
import android.content.Context;

public class PluginApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try{
            HookHelper.hookInstrumentation(base);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
