package com.hikvision.auto.apt_process;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.SOURCE)
public @interface Describe {
    String value();
    String invoke();
    String path();
    String returnType() default "";
}
