package com.hikvision.auto.router.base.business;

import com.hikvision.auto.router.base.business.android.IAndroid;
import com.hikvision.auto.router.base.business.peripheral.IPeripheral;
import com.hikvision.auto.router.base.business.recorder.IRecorder;

/**
 * 业务组件
 *
 * 场景分类：暂先分为以下几类业务场景
 * 1.行车记录仪相关业务场景
 * 2.平台相关业务场景
 * 3.本地处理业务场景
 * 4.外设先关业务场景
 * 5.MCU相关业务场景
 *
 * 同步异步：
 * 1.IModuleProcessor的实现类会根据方法的返回类型来决定调用方法同步还是异步的执行
 *   a)如果返回值 是void.class，则会在线程池中执行方法，则通过入参增加一个回调来通知调用者 执行结果
 *   b)如果返回值不是void.class，则会在调用者线程中执行，阻塞，直到结果的返回，可能会导致anr。
 *
 * 在源码环境下 HikProtocol 外层目录执行 chmod -R 777 HikProtocol/
 * 以防止生成业务路由表失败
 */
public interface IModuleProcessor {

    /**
     * 获取行车记录仪相关的业务逻辑处理接口
     * @return {@link IRecorder}
     */
    IRecorder recorderBusiness();

    /**
     * 获取Android本地处理业务逻辑接口
     * @return
     */
    IAndroid iAndroidBusiness();

    /**
     * 获取外设相关业务处理逻辑接口
     * @return
     */
    IPeripheral iPeripheralBusiness();


}
