package com.enzuo.ioc.bean.listener;

import com.enzuo.ioc.bean.Person;
import com.enzuo.ioc.bean.annotation.Application;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Classname TestListener
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 21:57
 * @Created by Enzuo
 */
@Slf4j
public class TestListener implements ApplicationListener{
    public TestListener(ApplicationContext context){
        context.addImportClass(Person.class);
    }
    @Override
    public void starting() {
        System.out.println("1start");
    }
}
