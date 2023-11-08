package com.enzuo.ioc.bean.listener;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.env.Environment;
import com.enzuo.ioc.bean.utils.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Classname ApplicationListeners
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 16:38
 * @Created by Enzuo
 */
public class ApplicationListeners {
    private List<ApplicationListener> list;
    private ApplicationListeners(){}
    public ApplicationListeners(Class<?>[] classes, ApplicationContext context) {
        list = new ArrayList<>();
        for (Class<?> clazz : classes) {
            try{
                Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(ApplicationContext.class);
                ApplicationListener o = (ApplicationListener)declaredConstructor.newInstance(context);
                list.add(o);
            }catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                    IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public void starting() {
        for (ApplicationListener applicationListener : list) {
            applicationListener.starting();
        }
    }

    public void environmentPrepared(Environment environment) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.environmentPrepared(environment);
            if(ObjectUtils.isNull(environment)){
                environment=new Environment();
            }
            if(ObjectUtils.isStringEmpty(environment.getAddress())){
                environment.setAddress("0.0.0.0");
            }
        }
    }

    public void contextPrepared(ApplicationContext context) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.contextPrepared(context);
        }
    }

    public void contextLoaded(ApplicationContext context) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.contextLoaded(context);
        }
    }

    public void started(ApplicationContext context) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.started(context);
        }
    }

    public void running(ApplicationContext context) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.running(context);
        }
    }

    public void failed(ApplicationContext context, Throwable exception) {
        for (ApplicationListener applicationListener : list) {
            applicationListener.failed(context,exception);
        }
    }


}
