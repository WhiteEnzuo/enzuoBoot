package com.enzuo.ioc.bean;

import com.enzuo.ioc.bean.annotation.*;
import com.enzuo.ioc.bean.annotation.aop.Aspect;
import com.enzuo.ioc.bean.annotation.aop.Before;
import com.enzuo.ioc.bean.application.BootApplication;
import com.enzuo.ioc.bean.context.impl.RunApplicationContext;
import com.enzuo.ioc.bean.utils.AnnotationUtils;

import java.util.List;
import java.util.Optional;

/**
 * @Classname Main
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 22:52
 * @Created by Enzuo
 */
//@EnableTest
@Application
public class Main {
    public static void main(String[] args) {
        BootApplication.run(Main.class,args);

//        System.out.println(
//                runApplicationContext
//                        .getBeanFactory()
//                        .getBeanName());
////        Object bean = runApplicationContext.getBean("person");
////        System.out.println(bean);
//        Person bean = runApplicationContext
//                .getBean(Person.class);
//        System.out.println(runApplicationContext.getBean(Test.class));
//
//        bean.test();
    }
}


