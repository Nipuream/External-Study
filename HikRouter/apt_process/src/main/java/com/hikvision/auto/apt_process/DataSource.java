package com.hikvision.auto.apt_process;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.SOURCE)
public @interface DataSource {

    String uri() default  "";
    String type() default "";
    String table() default "";
}
