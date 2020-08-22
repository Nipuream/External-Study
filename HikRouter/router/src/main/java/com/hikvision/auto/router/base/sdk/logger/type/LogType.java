package com.hikvision.auto.router.base.sdk.logger.type;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志类型
 */
@IntDef({

        LogType.NOTE,
        LogType.WARN,
        LogType.ERROR
})
@Retention(value = RetentionPolicy.CLASS)
@Target(value = ElementType.PARAMETER)
public @interface LogType {

    int NOTE = 0;

    int ERROR = 1;

    int WARN = 2;
}
