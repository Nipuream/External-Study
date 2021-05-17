package com.hikvision.auto.router.base.protocolex.comm;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({
        ExternalProtocol.QINGDAO, // router/files/青岛/青岛市巡游出租汽车运营专用设备及标志技术指引.pdf
})
@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.CLASS)
public @interface ExternalProtocol {

    int QINGDAO = 0;
}
