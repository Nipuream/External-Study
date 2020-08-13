package com.nipuream.plugintest;

import android.app.Instrumentation;
import android.content.Context;

import com.nipuream.plugintest.util.ReflectUtil;

public class HookHelper {

    public static final String TARGET_INTENT = "target_intent";

    public static void hookInstrumentation(Context context) throws Exception {
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        Object activityThread = ReflectUtil.getField(contextImplClass, context, "mMainThread");
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object mInstrumentation = ReflectUtil.getField(activityThreadClass, activityThread, "mInstrumentation");
        //用代理Instrumentation来替代mMainThread中的mInstrumentation，从而接管Instrumentation的任务
        ReflectUtil.setField(activityThreadClass, activityThread, "mInstrumentation", new InstrumentationProxy((Instrumentation) mInstrumentation, context.getPackageManager()));
    }

}
