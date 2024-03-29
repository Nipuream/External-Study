package com.hikvision.auto.apt_process;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface TypeDefine {
    String value();
    String define();
    String repeat() default  "";
}
