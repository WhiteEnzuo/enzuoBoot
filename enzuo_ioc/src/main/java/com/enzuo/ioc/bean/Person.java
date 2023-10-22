package com.enzuo.ioc.bean;

/**
 * @Classname Person
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 11:24
 * @Created by Enzuo
 */
public class Person {
    public Test test(){
        System.out.println("test");
        return new Test();
    }
}
class Test{}