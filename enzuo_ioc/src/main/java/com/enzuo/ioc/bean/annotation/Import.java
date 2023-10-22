package com.enzuo.ioc.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Import
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/21 22:02
 * @Created by Enzuo
 */
@Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Import {
    Class<?> value() ;
}
