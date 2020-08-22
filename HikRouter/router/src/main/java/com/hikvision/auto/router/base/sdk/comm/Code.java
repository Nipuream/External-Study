package com.hikvision.auto.router.base.sdk.comm;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 错误码
 */
public class Code {

    @IntDef({
            PROCESS_CODE.SUCCESSFUL,
            PROCESS_CODE.FAILED
    })
    @Retention(value = RetentionPolicy.CLASS)
    public @interface PROCESS_CODE {
        int SUCCESSFUL = 1;
        int FAILED = -1;
    }


}
