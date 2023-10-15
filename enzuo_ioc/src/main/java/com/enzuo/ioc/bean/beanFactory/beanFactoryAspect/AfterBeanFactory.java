package com.enzuo.ioc.bean.beanFactory.beanFactoryAspect;

/**
 * @Classname AfterBeanFactory
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 22:05
 * @Created by Enzuo
 */

public interface AfterBeanFactory {
    public Object afterBeanFactoryMake(String name,Object bean);

}
