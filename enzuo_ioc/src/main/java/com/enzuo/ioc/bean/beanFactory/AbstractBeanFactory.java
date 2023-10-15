package com.enzuo.ioc.bean.beanFactory;

import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.PostBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname BeanFactory
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 19:44
 * @Created by Enzuo
 */

public abstract class AbstractBeanFactory {
    Lock lock = new ReentrantLock();

    public abstract Object getBean(String beanName);

    public abstract <T> T getBean(String beanName, Class<T> clazz);

    public abstract <T> T getBean(Class<T> clazz);

    public abstract <T> Map<String, T> getBeanMap(Class<T> clazz);

    public abstract <T> List<T> getBeanList(Class<T> clazz);

    public abstract boolean registerBeanFunctionInterface(String name, Class<?> clazz);

    public abstract boolean initBeanFactory(List<PostBeanFactory> postBeanFactories, List<AfterBeanFactory> afterBeanFactories);

    public abstract boolean registerSingletonBean(String name, Object bean);

    public abstract boolean registerNormalBean(String name, Object bean);

    public abstract boolean isSingletonBean(String name);

    public abstract boolean isSingletonBean(Class<?> clazz);

    public abstract Object beanAspect(Object bean);


    public boolean freezeBeanIoc() {
        return lock.tryLock();
    }

    public boolean unfreezeBeanIoc() {
        lock.unlock();
        return true;
    }
}

