package com.hikvision.auto.router.base.sdk.recorder.type;

import android.support.annotation.IntDef;
import com.hikauto.sdk.record.constant.RecordMediaEventTypeConstants;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_ALL,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DEFAULT,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_G_SENSOR,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_PLATFORM,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_WIRED,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_WIRELESS,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_USER,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_BLACK,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_FRAME,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DBA,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_ADAS,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_TAXI_FULL,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_FACE,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_EVALUATE,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_ACC,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_SIGN_IN_OUT,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_TIME,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_TAXI_EMPTY,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_OUT_CITY,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_HIGH_SPEED,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_UNLEGAL_OPERATION,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DBA_TIRED,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DBA_DISTRACT,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DBA_SMOKE,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DBA_CALL,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_CALL_POLICE,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_FACE_NOT_MATCH,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_ABNORMAL_DUTY,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_LONG_TIME_STOP,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_APP_LOCK,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_DOUBLE_BUTTON,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_CANCEL_BUTTON,
        RecordMediaEventTypeConstants.MEDIA_EVENT_TYPE_FACE_CAPTURE

})
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface EventType {

    /**
     * ALL
     */
    int MEDIA_EVENT_TYPE_ALL = -1;

    /**
     * 普通
     */
    int MEDIA_EVENT_TYPE_DEFAULT = 0;

    /**
     * 紧急，gsensor
     */
    int MEDIA_EVENT_TYPE_G_SENSOR = 1;

    /**
     * 平台
     */
    int MEDIA_EVENT_TYPE_PLATFORM = 2;

    /**
     * 有线报警
     */
    int MEDIA_EVENT_TYPE_WIRED = 3;

    /**
     * 无线报警
     */
    int MEDIA_EVENT_TYPE_WIRELESS = 4;

    /**
     * 用户抓拍
     */
    int MEDIA_EVENT_TYPE_USER = 5;

    /**
     * 黑名单
     */
    int MEDIA_EVENT_TYPE_BLACK = 6;
    /**
     * 抽帧
     */
    int MEDIA_EVENT_TYPE_FRAME = 7;
    /**
     * DBA
     */
    int MEDIA_EVENT_TYPE_DBA = 8;
    /**
     * ADAS
     */
    int MEDIA_EVENT_TYPE_ADAS = 9;
    /**
     * 人脸触发
     */
    int MEDIA_EVENT_TYPE_FACE = 10;
    /**
     * 进入重车拍照
     */
    int MEDIA_EVENT_TYPE_TAXI_FULL = 11;
    /**
     * 服务评价拍照
     */
    int MEDIA_EVENT_TYPE_EVALUATE = 12;
    /**
     * acc 状态改变拍照
     */
    int MEDIA_EVENT_TYPE_ACC = 13;
    /**
     * 签到签退拍照
     */
    int MEDIA_EVENT_TYPE_SIGN_IN_OUT = 14;
    /**
     * 定时拍照
     */
    int MEDIA_EVENT_TYPE_TIME = 15;
    /**
     * 进入空车拍照
     */
    int MEDIA_EVENT_TYPE_TAXI_EMPTY = 16;
    /**
     * 出城拍照
     */
    int MEDIA_EVENT_TYPE_OUT_CITY = 17;
    /**
     * 超速拍照
     */
    int MEDIA_EVENT_TYPE_HIGH_SPEED = 18;
    /**
     * 违规运营拍照
     */
    int MEDIA_EVENT_TYPE_UNLEGAL_OPERATION = 19;
    /**
     * 疲劳驾驶拍照
     */
    int MEDIA_EVENT_TYPE_DBA_TIRED = 20;
    /**
     * 分心驾驶拍照
     */
    int MEDIA_EVENT_TYPE_DBA_DISTRACT = 21;
    /**
     * 开车抽烟拍照
     */
    int MEDIA_EVENT_TYPE_DBA_SMOKE = 22;
    /**
     * 开车打电话拍照
     */
    int MEDIA_EVENT_TYPE_DBA_CALL = 23;
    /**
     * 劫持报警拍照
     */
    int MEDIA_EVENT_TYPE_CALL_POLICE = 24;
    /**
     * 驾驶员人脸识别不匹配拍照
     */
    int MEDIA_EVENT_TYPE_FACE_NOT_MATCH = 25;
    /**
     * 异常交班拍照
     */
    int MEDIA_EVENT_TYPE_ABNORMAL_DUTY = 26;
    /**
     * 载客状态长时间停留拍照
     */
    int MEDIA_EVENT_TYPE_LONG_TIME_STOP = 27;
    /**
     * 锁存前三分钟和后三分钟录像
     */
    int MEDIA_EVENT_TYPE_APP_LOCK = 28;
    /**
     * 随手拍录像
     */
    int MEDIA_EVENT_TYPE_DOUBLE_BUTTON = 29;
    /**
     * 取消紧急报警按钮触发的报警
     */
    int MEDIA_EVENT_TYPE_CANCEL_BUTTON = 30;
    /**
     * 人脸抓拍事件
     */
    int MEDIA_EVENT_TYPE_FACE_CAPTURE = 40;
}
