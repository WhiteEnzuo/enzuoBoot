package com.enzuo.orm.annotation;

import com.enzuo.ioc.bean.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Table
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 09:16
 * @Created by Enzuo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value() default "";
}
