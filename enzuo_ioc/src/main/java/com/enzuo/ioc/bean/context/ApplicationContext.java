package com.enzuo.ioc.bean.context;

import com.enzuo.ioc.bean.annotation.Bean;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.PostBeanFactory;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @Classname ApplicationContext
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/4 18:59
 * @Created by Enzuo
 */

public abstract class ApplicationContext {
    private AbstractBeanFactory beanFactory;

    public ApplicationContext() {
        beanFactory = new BeanFactory();
    }

    public abstract Object getBean(String beanName);

    public abstract <T> T getBean(String beanName, Class<T> clazz);

    public abstract <T> T getBean(Class<T> clazz);

    public abstract <T> Map<String, T> getBeanMap(Class<T> clazz);

    public abstract <T> List<T> getBeanList(Class<T> clazz);

    public AbstractBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public abstract void init(List<PostBeanFactory> postBeanFactories, List<AfterBeanFactory> afterBeanFactories);
    public abstract Class<?> getClazz();
    public abstract void addImportClass(Class<?> clazz);
    public abstract void addExecuteImportClass(Class<?> clazz);

    public abstract String[] getArgs();
}
