package com.hikvision.auto.router;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.util.Log;
import com.hikvision.auto.apt_process.Autowire;
import com.hikvision.auto.router.base.business.IModuleProcessor;
import com.hikvision.auto.router.base.business.android.IAndroid;
import com.hikvision.auto.router.base.business.recorder.IRecorder;
import com.hikvision.auto.router.base.data.IData;
import com.hikvision.auto.router.base.platform.IPlatformComm;
import com.hikvision.auto.router.base.protocol.IProtocolProcessor;
import com.hikvision.auto.router.base.protocolex.IProtocolExternal;
import com.hikvision.auto.router.base.protocolex.comm.ExternalProtocol;
import com.hikvision.auto.router.base.protocolex.taximeter.ITaxiMeter;
import com.hikvision.auto.router.base.sdk.HIKSDK;
import com.hikvision.auto.router.dao.DaoMaster;
import com.hikvision.auto.router.utils.RefectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Properties;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class HikRouter {

    private static final String TAG = HikRouter.class.getSimpleName();
    /**
     * 业务组件操作本地文件夹路径
     */
    public static final String OP_DIR = "/sdcard/Hik/";
    /**
     * 请求超时时间
     */
    public static final int TIME_OUT = 10 * 1000;

    @IntDef({
            PLUGIN_FLAG.COMPATIBLE_SDK_PLUGIN,
            PLUGIN_FLAG.DATA_PLUGIN,
            PLUGIN_FLAG.EXTERNAL_PARSER_PLUGIN,
            PLUGIN_FLAG.BUSINESS_PROCESSOR_PLUGIN,
            PLUGIN_FLAG.PLATFORM_PLUGIN,
            PLUGIN_FLAG.PLATFORM_PARSER_PLUGIN
    })
    @Retention(value = RetentionPolicy.CLASS)
    @Target(value = ElementType.PARAMETER)
    public @interface PLUGIN_FLAG {
        /**
         * 兼容sdk组件
         */
        int COMPATIBLE_SDK_PLUGIN = 0x000001;
        /**
         * 数据组件
         */
        int DATA_PLUGIN = 0x000002;
        /**
         * 外设协议解析组件
         */
        int EXTERNAL_PARSER_PLUGIN = 0x000004;
        /**
         * 业务处理组件
         */
        int BUSINESS_PROCESSOR_PLUGIN = 0x000008;
        /**
         * 平台组件
         */
        int PLATFORM_PLUGIN = 0x000010;
        /**
         * 平台协议解析组件
         */
        int PLATFORM_PARSER_PLUGIN = 0x000020;
    }


    private volatile static HIKSDK sdk;
    private volatile static IModuleProcessor processor;
    private volatile static IProtocolProcessor protocolParser;
    private volatile static IPlatformComm platformComm;
    private volatile static IData data;
    private volatile static IProtocolExternal external;
    private static Properties properties = new Properties();

    /**
     * 初始化路由
     *
     * @return
     */
    public static boolean initRouter() {

        boolean initRes = true;
        InputStream in = null;

        try {
            in = RouterApp.getRouterApp().getAssets().open("router.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            initRes = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return initRes;
    }

    /**
     * 获取兼容sdk接口
     * 防止基线组sdk修改导致代码大量改动
     *
     * @return
     */
    static HIKSDK compatibleSdk() {

        if (sdk == null) {
            synchronized (HikRouter.class) {
                try {
                    Class cls = Class.forName(properties.getProperty("ICompatibleSDK"));
                    sdk = (HIKSDK) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sdk;
    }

    /**
     * 获取业务处理接口
     * 隔离业务代码
     *
     * @return
     */
    static IModuleProcessor moduleProcessor() {

        if (processor == null) {
            synchronized (HikRouter.class) {
                try {
                    Class cls = Class.forName(properties.getProperty("IModuleProcessor"));
                    processor = (IModuleProcessor) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return processor;
    }

    /**
     * 获取协议解析接口
     * 为了设配各级地级市协议差异性
     *
     * @return
     */
    static IProtocolProcessor protocolParser() {

        if (protocolParser == null) {
            synchronized (HikRouter.class) {
                try {
                    Class cls = Class.forName(properties.getProperty("IProtocolParser"));
                    protocolParser = (IProtocolProcessor) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return protocolParser;
    }

    /**
     * 获取平台通信接口
     *
     * @return
     */
    static IPlatformComm platformComm() {

        if (platformComm == null) {
            synchronized (HikRouter.class) {
                try {
                    Class cls = Class.forName(properties.getProperty("IPlatformComm"));
                    platformComm = (IPlatformComm) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return platformComm;
    }

    /**
     * 获取数据提供模块接口
     *
     * @return
     */
    static IData data() {

        if (data == null) {
            synchronized (HikRouter.class) {
                try {
                    Class cls = Class.forName(properties.getProperty("IData"));
                    data = (IData) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 获取外设数据模块接口
     * @return
     */
    static IProtocolExternal external(){

        if(external == null){
            synchronized (HikRouter.class){
                try{
                    Class cls = Class.forName(properties.getProperty("IProtocolExParser"));
                    external = (IProtocolExternal) cls.newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return external;
    }

    /**
     * 初始化数据库
     */
    public static void installDb() {

        PackageInfo packageInfo = null;
        try {
            packageInfo = RouterApp.getRouterApp().getPackageManager().getPackageInfo(
                    RouterApp.getRouterApp().getPackageName(), PackageManager.GET_PROVIDERS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageInfo == null) {
            return;
        }
        ProviderInfo[] infos = packageInfo.providers;
        boolean isExits = false;
        if (infos != null) {
            Printer.debug(TAG, "infos : " + infos.toString());
            for (ProviderInfo info : infos) {
                if (info.name.contains(properties.getProperty("DataProvider"))) {
                    isExits = true;
                    break;
                }
            }
        } else {
            Printer.debug(TAG, "infos is empty.");
        }

        if (!isExits) {
            throw new RuntimeException("清单文件中不存在 DataProvider.");
        }

        Field field = null;
        try {
            field = RouterApp.class.getDeclaredField("db");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (field == null) {
            return;
        }

        field.setAccessible(true);
        try {
            if (field.get(RouterApp.getRouterApp()) != null) {
                throw new RuntimeException("database session have been initial.");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (isExits) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RouterApp.getRouterApp(), "router.db");
            SQLiteDatabase database = helper.getWritableDatabase();
            try {
                field.set(RouterApp.getRouterApp(), database);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态加载组件
     *
     * @param classLoader 宿主classloader
     * @param context     宿主上下文环境
     * @param flag        需要加载哪些组件  @see {@link PLUGIN_FLAG}
     * @throws Exception
     */
    public static void loadPlugin(ClassLoader classLoader, Context context, int flag) throws Exception {


        if ((flag & PLUGIN_FLAG.DATA_PLUGIN) != 0) {
            Log.i("HikRouter", "load data plugin.");
            loadPluginDex("system/framework/data.dex", classLoader, context);
        }

        if ((flag & PLUGIN_FLAG.COMPATIBLE_SDK_PLUGIN) != 0) {
            Log.i("HikRouter", "load compatible sdk plugin.");
            loadPluginDex("system/framework/sdk.dex", classLoader, context);
        }

        if ((flag & PLUGIN_FLAG.BUSINESS_PROCESSOR_PLUGIN) != 0) {
            Log.i("HikRouter", "load business plugin.");
            loadPluginDex("system/framework/business.dex", classLoader, context);
        }

        //todo 未完待续
    }

    private static void loadPluginDex(String path, ClassLoader hostClassLoader, Context context) throws Exception {

        DexClassLoader pluginClassLoader = new DexClassLoader(path, context.getCacheDir().getAbsolutePath(),
                null, hostClassLoader);
        Object pluginDexPathList = RefectUtil.getField(BaseDexClassLoader.class, pluginClassLoader, "pathList");
        Object pluginElements = RefectUtil.getField(Class.forName("dalvik.system.DexPathList"), pluginDexPathList, "dexElements");
        Object hostPathList = RefectUtil.getField(BaseDexClassLoader.class, hostClassLoader, "pathList");
        Object hostElements = RefectUtil.getField(Class.forName("dalvik.system.DexPathList"), hostPathList, "dexElements");
        Object array = combineArray(hostElements, pluginElements);
        RefectUtil.setField(Class.forName("dalvik.system.DexPathList"), hostPathList, "dexElements", array);
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

    /**
     * 动态注入
     *
     * @param object
     */
    public static void inject(Object object) {

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            Autowire autowire = field.getAnnotation(Autowire.class);
            if (autowire != null) {
                Object findObj = find(autowire.path());

                if (findObj == null) {
                    Printer.debug(TAG, "can't find need path object, check out you path !");
                    return;
                }
                try {
                    field.setAccessible(true);
                    field.set(object, findObj);
                    Printer.debug(TAG, "object : " + object.getClass() + ", set object successful.");
                } catch (Exception e) {
                    Printer.debug(TAG, "inject Autowire field failed.");
                }
            }
        }
    }

    /**
     * 切换外设协议
     * @param protocol
     */
    public static void switchExternalProtocol(@ExternalProtocol int protocol){
        external().defineProtocol(protocol);
    }

    private static Object find(String path) {
        Object obj = null;
        switch (path) {
            case IRecorder.PATH:
                obj = moduleProcessor().recorderBusiness();
                break;
            case IAndroid.PATH:
                obj = moduleProcessor().iAndroidBusiness();
                break;
            case IData.PATH:
                obj = data();
                break;
            case HIKSDK.PATH:
                obj = compatibleSdk();
                break;
            case ITaxiMeter.PATH:
                obj = external().taximeter();
                break;
        }
        return obj;
    }


}
