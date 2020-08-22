package com.hikvision.auto.router.base.sdk.system.type;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 电话监听模式
 */
@IntDef({
        CallType.normal,
        CallType.monitor
})
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.PARAMETER})
public @interface CallType {
    /**
     * 普通电话
     */
    int normal = 0x01;

    /**
     * 监听电话
     */
    int monitor = 0x02;
}
