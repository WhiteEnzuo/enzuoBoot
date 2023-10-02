package com.enzuo.ioc.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @interfaceName First
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 22:25
 * @Created by Enzuo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface First {
}
