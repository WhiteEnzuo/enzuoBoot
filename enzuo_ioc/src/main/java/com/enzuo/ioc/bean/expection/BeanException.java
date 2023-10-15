package com.enzuo.ioc.bean.expection;

/**
 * @Classname NotBeanExpection
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/3 7:52
 * @Created by Enzuo
 */

public class BeanException extends Exception {
    public BeanException(String msg) {
        super(msg);
    }
}

