package com.enzuo.ioc.bean;

import com.enzuo.ioc.bean.annotation.Autowired;
import com.enzuo.ioc.bean.annotation.Bean;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;

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
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.registerBeanFunctionInterface("person",Person.class);
        beanFactory.registerBeanFunctionInterface("person1",Person1.class);
        beanFactory.initBeanFactory(new ArrayList<>(),new ArrayList<>());
        Person bean = beanFactory.getBean(Person.class);
        System.out.println(bean.person1);
    }
}
class Person{
    @Autowired
    Person1 person1;
}
class Person1{
    @Autowired
    Person person;
    @Autowired
    BeanFactory beanFactory;

    @Override
    public String toString() {
        return "Person1{" +
                "person=" + person +
                ", beanFactory=" + beanFactory +
                '}';
    }
}
