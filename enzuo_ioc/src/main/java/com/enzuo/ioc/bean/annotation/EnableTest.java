package com.enzuo.ioc.bean.annotation;

import com.enzuo.ioc.bean.Main;
import com.enzuo.ioc.bean.Person;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname EnableTest
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/21 22:21
 * @Created by Enzuo
 */
@Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(Person.class)
public @interface EnableTest {
}
