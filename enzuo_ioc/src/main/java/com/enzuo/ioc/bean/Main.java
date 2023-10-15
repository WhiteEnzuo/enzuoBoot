package com.enzuo.ioc.bean;

import com.enzuo.ioc.bean.annotation.Autowired;
import com.enzuo.ioc.bean.annotation.Component;
import com.enzuo.ioc.bean.annotation.aop.After;
import com.enzuo.ioc.bean.annotation.aop.Aspect;
import com.enzuo.ioc.bean.annotation.aop.Before;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;
import com.enzuo.ioc.bean.context.impl.RunApplicationContext;

import java.util.ArrayList;

/**
 * @Classname Main
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 22:52
 * @Created by Enzuo
 */

public class Main {
    public static void main(String[] args) {
        RunApplicationContext runApplicationContext =
                new RunApplicationContext(Main.class);
        System.out.println(
                runApplicationContext
                        .getBeanFactory()
                        .getBeanName());
//        Object bean = runApplicationContext.getBean("person");
//        System.out.println(bean);
        Person bean = runApplicationContext
                .getBean(Person.class);
        System.out.println(runApplicationContext.getBean(Test.class));

        bean.test();

    }
}

@Component
class Person {
    @Autowired Test test;
    public void test() {
        System.out.println(test);
    }
}

@Aspect
@Component
class Test {
        @Before("com.enzuo.ioc.bean.Person")
    public void after() {
        System.out.println(12);
    }
}
