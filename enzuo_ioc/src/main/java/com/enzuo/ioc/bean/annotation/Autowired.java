package com.enzuo.ioc.bean.annotation;

import javax.annotation.Resource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @interfaceName AutoWited
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 21:40
 * @Created by Enzuo
 */
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
