package com.enzuo.ioc.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Application
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 11:35
 * @Created by Enzuo
 */
@Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
    Class<?>[] execute()default {};
    Class<?>[] importClass()default {};
}
