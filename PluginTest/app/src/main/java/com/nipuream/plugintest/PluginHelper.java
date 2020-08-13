package com.nipuream.plugintest;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import com.nipuream.plugintest.util.ReflectUtil;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class PluginHelper {

    private static final String TAG = PluginHelper.class.getName();

    private static final String CLASS_DEX_PATH_LIST = "dalvik.system.DexPathList";
    private static final String FIELD_PATH_LIST = "pathList";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";
    private static final String PLUGIN_APK_PATH = "/sdcard/app-debug.apk";

    public static void loadPluginClass(Context context, ClassLoader hostClassLoader) throws Exception {

        // step1. download plugin apk.
        File pluginFile = new File(PLUGIN_APK_PATH);

        if(pluginFile.exists()){
            Log.i(TAG,"plugin file exits, path : "+ pluginFile.getAbsolutePath());
            Log.i(TAG,"cache dir :  "+context.getCacheDir().getAbsolutePath());
        }

        //step 2. create dexclassloader.
        DexClassLoader pluginClassloader = new DexClassLoader(pluginFile.getAbsolutePath(),
                context.getCacheDir().getAbsolutePath(), null,hostClassLoader);
        //step 3. 通过反射获取 PluginClassLoader pathList字段
        Object pluginDexPathList = ReflectUtil.getField(BaseDexClassLoader.class, pluginClassloader, FIELD_PATH_LIST);
        //step 4. 通过反射获取到 DexPathList的dexElements 字段
        Object pluginElements = ReflectUtil.getField(Class.forName(CLASS_DEX_PATH_LIST), pluginDexPathList, FIELD_DEX_ELEMENTS);
        //step 5. 通过反射获取到宿主工程中ClassLoader的pathList字段
        Object hostDexPathList = ReflectUtil.getField(BaseDexClassLoader.class, hostClassLoader, FIELD_PATH_LIST);
        //step 6. 通过反射获取到宿主工程中DexPathList的dexElements字段
        Object hostElements = ReflectUtil.getField(Class.forName(CLASS_DEX_PATH_LIST), hostDexPathList, FIELD_DEX_ELEMENTS);
        // step 7.将插件ClassLoader的dexElements合并到宿主ClassLoader的dexElements.
        Object array = combineArray(hostElements, pluginElements);
        //step 8. 将合并的dexElements 设置到宿主的classloader.
        ReflectUtil.setField(Class.forName(CLASS_DEX_PATH_LIST), hostDexPathList, FIELD_DEX_ELEMENTS, array);
    }

    public static Resources initPluginResource(Context context) throws Exception {

        Class<AssetManager> clazz = AssetManager.class;
        AssetManager assetManager = clazz.newInstance();
        Method method = clazz.getMethod("addAssetPath", String.class);
        method.invoke(assetManager, PLUGIN_APK_PATH);

        Resources pluginResource = new Resources( assetManager, context.getResources().getDisplayMetrics(),
                context.getResources().getConfiguration());

        return pluginResource;
    }

    private static Object combineArray(Object hostElements, Object pluginElements) {
        Class<?> componentType = hostElements.getClass().getComponentType();
        int i = Array.getLength(hostElements);
        int j = Array.getLength(pluginElements);
        int k = i + j;
        Object result = Array.newInstance(componentType, k);
        System.arraycopy(pluginElements, 0, result, 0, j);
        System.arraycopy(hostElements, 0, result, j, i);
        return result;
    }

}
