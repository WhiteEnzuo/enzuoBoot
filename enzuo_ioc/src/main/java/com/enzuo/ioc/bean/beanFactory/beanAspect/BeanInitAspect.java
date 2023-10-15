package com.enzuo.ioc.bean.beanFactory.beanAspect;

import com.enzuo.ioc.bean.annotation.Autowired;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname BeanInitAspect
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/3 11:51
 * @Created by Enzuo
 */

public class BeanInitAspect implements MethodInterceptor {
    private BeanFactory beanFactory;

    public BeanInitAspect(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("toString")) {
            return o.getClass().getSimpleName();

        }
        Autowired methodAutowired = method.getAnnotation(Autowired.class);
        if (ObjectUtils.isNotNull(methodAutowired)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            List<Object> parameters = new ArrayList<>();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Object bean = beanFactory.getBean(parameterType);
                parameters.add(i, bean);
            }
            return methodProxy.invokeSuper(o, parameters.toArray());
        } else {
            Parameter[] parameters = method.getParameters();
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Autowired parameterAutowired = parameter.getAnnotation(Autowired.class);
                if (ObjectUtils.isNull(parameterAutowired)) {
                    args.add(i, objects[i]);
                    continue;
                }
                if (parameterAutowired.value().equals("")) {
                    Object bean = beanFactory.getBean(parameter.getType());
                    args.add(i, bean);
                    continue;
                }
                Object bean = beanFactory.getBean(parameterAutowired.value());
                args.add(i, bean);
            }

            return methodProxy.invokeSuper(o, args.toArray());

        }
    }
}
