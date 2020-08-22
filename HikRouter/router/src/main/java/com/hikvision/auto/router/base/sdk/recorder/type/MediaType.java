package com.hikvision.auto.router.base.sdk.recorder.type;

import android.support.annotation.IntDef;
import com.hikauto.sdk.record.constant.RecordMediaTypeConstants;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        RecordMediaTypeConstants.VIDEO,
        RecordMediaTypeConstants.IMAGE,
        RecordMediaTypeConstants.AUDIO,
        RecordMediaTypeConstants.ALL
})
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface MediaType {
    /**
     * 视频
     */
    int VIDEO = 0;

    /**
     * 图片
     */
    int IMAGE = 1;

    /**
     * 音频
     */
    int AUDIO = 2;

    /**
     * ALL
     */
    int ALL = -1;
}
