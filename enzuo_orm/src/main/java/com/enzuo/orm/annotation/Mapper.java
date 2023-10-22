package com.enzuo.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Mapper
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/21 21:56
 * @Created by Enzuo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
}
