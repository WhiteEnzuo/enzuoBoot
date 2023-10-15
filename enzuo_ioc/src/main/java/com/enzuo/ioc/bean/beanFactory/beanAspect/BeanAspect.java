package com.enzuo.ioc.bean.beanFactory.beanAspect;

import com.enzuo.ioc.bean.annotation.aop.After;
import com.enzuo.ioc.bean.annotation.aop.AfterThrowing;
import com.enzuo.ioc.bean.annotation.aop.Before;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Classname BeanAspect
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/3 8:07
 * @Created by Enzuo
 */

public class BeanAspect implements MethodInterceptor {
    private Map<String, Map<Object, List<Method>>> map;
    private String className;
    private Class<?> clazz;
    private AbstractBeanFactory beanFactory;
    private Object bean;

    public BeanAspect(
            Map<String, Map<Object,
                    List<Method>>> map,
            Class<?> clazz,
            AbstractBeanFactory beanFactory,
            Object bean
    ) {
        this.map = map;
        this.className = clazz.getName();
        this.clazz = clazz;
        this.beanFactory = beanFactory;
        this.bean = bean;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        if (method.getName().equals("toString")) {
            return this.bean.getClass().getSimpleName();
        }
        Map<Object, List<Method>> errorMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (className.contains(key)) {
                value.forEach((object, aspectMethods) -> {
                    aspectMethods.forEach((aspectMethod -> {
                        AfterThrowing afterThrowing = aspectMethod.getAnnotation(AfterThrowing.class);
                        if (ObjectUtils.isNotNull(afterThrowing)) {
                            if (errorMap.containsKey(key)) {
                                errorMap.get(key).add(aspectMethod);
                            } else {
                                ArrayList<Method> list = new ArrayList<>();
                                list.add(aspectMethod);
                                errorMap.put(key, list);

                            }
                        }
                    }));

                });
            }
        });
        AtomicReference<Object> returnObject = new AtomicReference<>(null);
        map.forEach((key, value) -> {
            if (className.contains(key)) {
                value.forEach((object, aspectMethods) -> {
                    aspectMethods.forEach((aspectMethod -> {
                        aspectMethod.setAccessible(true);
                        After after = aspectMethod.getAnnotation(After.class);
                        Before before = aspectMethod.getAnnotation(Before.class);

                        try {
                            if (ObjectUtils.isNotNull(before)) {
                                Class<?>[] parameterTypes = aspectMethod.getParameterTypes();
                                List<Object> param = new ArrayList<>();
                                for (int i = 0; i < parameterTypes.length; i++) {
                                    Object bean = beanFactory.getBean(parameterTypes[i]);
                                    param.add(i, bean);
                                }
                                aspectMethod.invoke(object, param.toArray());

                            }
                            returnObject.set(methodProxy.invoke(this.bean, objects));
                            if (ObjectUtils.isNotNull(after)) {
                                Class<?>[] parameterTypes = aspectMethod.getParameterTypes();
                                List<Object> param = new ArrayList<>();
                                for (int i = 0; i < parameterTypes.length; i++) {
                                    Object bean = beanFactory.getBean(parameterTypes[i]);
                                    param.add(i, bean);
                                }
                                try {
                                    aspectMethod.invoke(object, param.toArray());
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();

                            errorMap.forEach((o1, errorMethods) -> {
                                try {
                                    for (Method errorMethod : errorMethods) {
                                        if (errorMethod.getParameterTypes().length > 0) {
                                            errorMethod.invoke(o1, e);
                                        } else {
                                            errorMethod.invoke(o1);
                                        }
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    System.exit(0);
                                }
                            });


                        }

                    }));

                });
            }
        });

        return returnObject.get();
    }
}
