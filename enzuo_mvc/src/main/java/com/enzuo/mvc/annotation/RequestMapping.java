package com.enzuo.mvc.annotation;


import com.enzuo.mvc.enums.HttpMethodsEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname RequestMapping
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 16:59
 * @Created by Enzuo
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "/";
    HttpMethodsEnum methods();
}
