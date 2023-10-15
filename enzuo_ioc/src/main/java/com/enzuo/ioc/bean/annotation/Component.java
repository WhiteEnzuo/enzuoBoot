package com.enzuo.ioc.bean.annotation;

import com.enzuo.ioc.bean.enums.BeanTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";

    BeanTypeEnum type() default BeanTypeEnum.SINGLETON_BEAN;
}
