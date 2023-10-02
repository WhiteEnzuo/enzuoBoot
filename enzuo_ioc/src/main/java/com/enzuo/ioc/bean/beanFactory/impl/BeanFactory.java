package com.enzuo.ioc.bean.beanFactory.impl;

import com.enzuo.ioc.bean.annotation.Autowired;
import com.enzuo.ioc.bean.annotation.First;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.beanFactory.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.PostBeanFactory;
import com.enzuo.ioc.bean.beanInterface.BeanFunctionInterface;
import com.enzuo.ioc.bean.utils.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Classname BeanFactory
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 20:57
 * @Created by Enzuo
 */

public class BeanFactory extends AbstractBeanFactory {
    //一级缓存，存放Bean的生产方法
    private final Map<String, BeanFunctionInterface> beanFunctionContext = new HashMap<>();
    private final Map<Class<?>, BeanFunctionInterface> beanFunctionContextByClass = new HashMap<>();

    //二级缓存，存放未初始化的Bean
    private final Map<String, Object> earlySingletonBeanClass = new HashMap<>();

    //三级缓存，存放完整的Bean
    private final Map<String, Object> beanContext = new HashMap<>();
    private final Map<Class<?>, Object> beanContextByClass = new HashMap<>();
    private final Map<Class<?>, List<Object>> beanListContextByClass = new HashMap<>();
    private final Map<Class<?>, Map<String, Object>> beanMapContextByClass = new HashMap<>();

    public BeanFactory() {
        registerSingletonBean("beanFactory", this);
    }

    @Override
    public Object getBean(String beanName) {
        String name = beanName.toLowerCase(Locale.ROOT);
        if (isSingletonBean(name)) {
            return beanContext.get(name);
        }
        if (beanFunctionContext.containsKey(name)) {
            return null;
        }
        BeanFunctionInterface beanFunctionInterface = beanFunctionContext.get(name);
        if (ObjectUtils.isNull(beanFunctionInterface)) {
            return null;
        }
        return beanFunctionInterface.getObject();
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        if (isSingletonBean(beanName)) {
            return (T) beanContext.get(beanName);
        }
        if (beanFunctionContext.containsKey(beanName)) {
            return null;
        }
        BeanFunctionInterface beanFunctionInterface = beanFunctionContext.get(beanName);
        if (ObjectUtils.isNull(beanFunctionInterface)) {
            return null;
        }
        return (T) beanFunctionInterface.getObject();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (isSingletonBean(clazz)) {
            return (T) beanContextByClass.get(clazz);
        }
        if (beanFunctionContextByClass.containsKey(clazz)) {
            return null;
        }
        BeanFunctionInterface beanFunctionInterface = beanFunctionContextByClass.get(clazz);
        if (ObjectUtils.isNull(beanFunctionInterface)) {
            return null;
        }
        return (T) beanFunctionInterface.getObject();
    }

    @Override
    public <T> Map<String, T> getBeanMap(Class<T> clazz) {
        if (beanMapContextByClass.containsKey(clazz)) {
            return (Map<String, T>) beanMapContextByClass.get(clazz);
        }
        return new HashMap<>();
    }

    @Override
    public <T> List<T> getBeanList(Class<T> clazz) {
        if (beanMapContextByClass.containsKey(clazz)) {
            return (List<T>) beanListContextByClass.get(clazz);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean registerBeanFunctionInterface(String beanName, Class<?> clazz) {
        String name = beanName.toLowerCase(Locale.ROOT);
        AtomicReference<Boolean> isSuccess = new AtomicReference<>(true);
        BeanFunctionInterface beanFunctionInterface = () -> {
            Object bean = null;
            try {
                if (isSingletonBean(name)) {
                    return beanContext.get(name);
                }
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);

                bean = constructor.newInstance();
                earlySingletonBeanClass.put(name, bean);
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if (ObjectUtils.isNull(autowired)) {
                        continue;
                    }
                    if (!(beanContextByClass.containsKey(type) || beanContext.containsKey(autowired.value()))) {
                        if (earlySingletonBeanClass.containsKey(autowired.value())
                                || earlySingletonBeanClass.containsKey(type.getSimpleName().toLowerCase())) {
                            Object fieldBean = earlySingletonBeanClass.containsKey(autowired.value()) ?
                                    earlySingletonBeanClass.get(autowired.value()) :
                                    earlySingletonBeanClass.get(type.getSimpleName().toLowerCase());
                            field.set(bean, fieldBean);
                            continue;
                        }
                        if (beanFunctionContext.containsKey(autowired.value())
                                || beanFunctionContext.containsKey(type.getSimpleName().toLowerCase())) {
                            BeanFunctionInterface fieldBeanFunction = beanFunctionContext.containsKey(autowired.value()) ?
                                    beanFunctionContext.get(autowired.value()) :
                                    beanFunctionContext.get(type.getSimpleName().toLowerCase());
                            Object fieldBean = fieldBeanFunction.getObject();
                            field.setAccessible(true);
                            field.set(bean, fieldBean);
                            continue;
                        }
                        //全都失败了，说明代码有问题
                        isSuccess.set(false);
                        throw new RuntimeException("找不到" + field.getName() + "的Bean");
                    }
                    Object fieldBean = beanContextByClass.containsKey(type) ?
                            beanContextByClass.get(type) :
                            beanContext.get(autowired.value());
                    field.setAccessible(true);
                    field.set(bean, fieldBean);
                }
                if (beanContext.containsKey(name)) {
                    throw new RuntimeException("创建name为" + name + "的bean时候发现缓存已经存在了");
                }
                beanContext.put(name, bean);
                if (isPutBeanContextByClass(bean)) {
                    beanContextByClass.put(clazz, bean);
                }

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                isSuccess.set(false);
            }
            return bean;
        };
        beanFunctionContext.put(name, beanFunctionInterface);
        return isSuccess.get();
    }

    @Override
    public boolean initBeanFactory(List<PostBeanFactory> postBeanFactories, List<AfterBeanFactory> afterBeanFactories) {
        freezeBeanIoc();
        AtomicReference<Boolean> isSuccess = new AtomicReference<>(true);
        beanFunctionContext.forEach((beanName, beanFunctionInterface) -> {
            if (!isSuccess.get()) {
                return;
            }

            Object bean = beanFunctionInterface.getObject();
            if (ObjectUtils.isNull(bean)) {
                isSuccess.set(false);
                return;
            }
            postBeanFactories.forEach(postBeanFactory -> {
                Object o = postBeanFactory.postBeanFactoryMake(beanName, bean);
                if (ObjectUtils.isNotNull(o)) {
                    registerSingletonBean(beanName, o);
                }
            });
            List<Object> objectList;
            if (beanListContextByClass.containsKey(bean.getClass())) {
                objectList = beanListContextByClass.get(bean.getClass());
            } else {
                objectList = new ArrayList<>();
                beanListContextByClass.put(bean.getClass(), objectList);
            }
            objectList.add(bean);

            Map<String, Object> map;
            if (beanMapContextByClass.containsKey(bean.getClass())) {
                map = beanMapContextByClass.get(bean.getClass());
            } else {
                map = new HashMap<>();
                beanMapContextByClass.put(bean.getClass(), map);
            }
            map.put(beanName, bean);
            afterBeanFactories.forEach(afterBeanFactory -> {
                Object o = afterBeanFactory.afterBeanFactoryMake(beanName, bean);
                registerSingletonBean(beanName, o);
            });
        });
        unfreezeBeanIoc();
        return isSuccess.get();
    }

    @Override
    public boolean registerSingletonBean(String beanName, Object bean) {
        String name = beanName.toLowerCase(Locale.ROOT);
        beanContext.put(name, bean);
        if (!beanContextByClass.containsKey(bean.getClass())) {
            beanContextByClass.put(bean.getClass(), bean);
        }
        if (beanListContextByClass.containsKey(bean.getClass())) {
            List<Object> beanList = beanListContextByClass.get(bean.getClass());
            beanList.add(bean);
        } else {
            List<Object> beanList = new ArrayList<>();
            beanList.add(bean);
            beanListContextByClass.put(bean.getClass(), beanList);
        }
        if (beanMapContextByClass.containsKey(bean.getClass())) {
            Map<String, Object> map = beanMapContextByClass.get(bean.getClass());
            map.put(name, bean);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(name, bean);
            beanMapContextByClass.put(bean.getClass(), map);
        }
        return registerBeanFunctionInterface(name, bean.getClass());
    }

    @Override
    public boolean registerNormalBean(String beanName, Object bean) {
        String name = beanName.toLowerCase(Locale.ROOT);
        return registerBeanFunctionInterface(name, bean.getClass());
    }

    @Override
    public boolean isSingletonBean(String beanName) {
        String name = beanName.toLowerCase(Locale.ROOT);
        return beanContext.containsKey(name);
    }

    @Override
    public boolean isSingletonBean(Class<?> clazz) {
        return beanContextByClass.containsKey(clazz);
    }

    @Override
    public boolean freezeBeanIoc() {
        return super.freezeBeanIoc();
    }

    @Override
    public boolean unfreezeBeanIoc() {
        return super.unfreezeBeanIoc();
    }

    private boolean isPutBeanContextByClass(Object bean) {
        Class<?> clazz = bean.getClass();
        First first = clazz.getAnnotation(First.class);
        return !beanContextByClass.containsKey(clazz) || ObjectUtils.isNotNull(first);
    }
}

