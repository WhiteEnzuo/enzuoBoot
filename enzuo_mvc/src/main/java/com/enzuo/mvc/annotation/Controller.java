package com.enzuo.mvc.annotation;


import com.enzuo.ioc.bean.annotation.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Controller
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 16:54
 * @Created by Enzuo
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "/";

}
