package com.enzuo.ioc.bean.beanFactory.beanFactoryAspect;

/**
 * @interfaceName postBeanFactory
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 22:05
 * @Created by Enzuo
 */

public interface PostBeanFactory {
    public Object postBeanFactoryMake(String name,Object bean);
}
