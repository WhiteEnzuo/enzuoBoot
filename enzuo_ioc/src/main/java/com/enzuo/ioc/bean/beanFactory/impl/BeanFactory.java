package com.enzuo.ioc.bean.beanFactory.impl;

import com.enzuo.ioc.bean.annotation.Autowired;
import com.enzuo.ioc.bean.annotation.Component;
import com.enzuo.ioc.bean.annotation.First;
import com.enzuo.ioc.bean.annotation.aop.After;
import com.enzuo.ioc.bean.annotation.aop.AfterThrowing;
import com.enzuo.ioc.bean.annotation.aop.Before;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanAspect.BeanAspect;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanAspect.BeanInitAspect;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.PostBeanFactory;
import com.enzuo.ioc.bean.beanInterface.BeanFunctionInterface;
import com.enzuo.ioc.bean.enums.BeanTypeEnum;
import com.enzuo.ioc.bean.expection.BeanException;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.*;
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
    //AOP的缓存
    private final Map<String, Map<Object, List<Method>>> methodMap = new HashMap<>();

    // 一级缓存，存放Bean的生产方法
    private final Map<String, BeanFunctionInterface> beanFunctionContext = new HashMap<>();
    private final Map<Class<?>, BeanFunctionInterface> beanFunctionContextByClass = new HashMap<>();

    // 二级缓存，存放未初始化的Bean
    private final Map<String, Object> earlySingletonBeanClass = new HashMap<>();

    // 三级缓存，存放完整的Bean
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
    public Iterable<String> getBeanName() {
        return beanFunctionContext.keySet();
    }

    @Override
    public Iterable<Object> getBean() {
        return beanContext.values();
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
            Class<?> newClazz = clazz;
            Object bean = null;
            try {
                if (isSingletonBean(name)) {
                    return beanContext.get(name);
                }

                if (isNeedInitAspect(newClazz)) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(newClazz);
                    enhancer.setCallback(new BeanInitAspect(this));
                    bean = enhancer.create();
                } else {
                    Constructor<?> constructor = newClazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    bean = constructor.newInstance();
                }
                Component annotation = newClazz.getAnnotation(Component.class);

                if (ObjectUtils.isNull(annotation) || annotation.type().equals(BeanTypeEnum.SINGLETON_BEAN)) {
                    earlySingletonBeanClass.put(name, bean);
                }
                Field[] declaredFields = newClazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if (ObjectUtils.isNull(autowired)) {
                        continue;
                    }
                    //Bean A -> Bean B
                    //Bean B -> Bean A
                    Object fieldBean = makeBean(type, autowired);
                    field.setAccessible(true);
                    field.set(bean, fieldBean);
                }

                if (isNeedAspect(clazz)) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(clazz);
                    enhancer.setCallback(new BeanAspect(methodMap, clazz,
                            this, bean));
                    bean = enhancer.create();
                }


                if (beanContext.containsKey(name)) {
                    throw new BeanException("创建name为" + name + "的bean时候发现缓存已经存在了");
                }
                if (ObjectUtils.isNull(annotation) || annotation.type().equals(BeanTypeEnum.SINGLETON_BEAN)) {

                    putBean(name, clazz, bean);
                }


            } catch (BeanException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                isSuccess.set(false);
            }
            return bean;
        };
        putBeanFunction(name, clazz, beanFunctionInterface);
        return isSuccess.get();
    }

    private Object makeBean(Class<?> type, Autowired autowired) throws BeanException {
        if (!(beanContextByClass.containsKey(type) || beanContext.containsKey(autowired.value()))) {
            if (earlySingletonBeanClass.containsKey(autowired.value())
                    || earlySingletonBeanClass.containsKey(type.getSimpleName().toLowerCase())) {
                return earlySingletonBeanClass.containsKey(autowired.value()) ?
                        earlySingletonBeanClass.get(autowired.value()) :
                        earlySingletonBeanClass.get(type.getSimpleName().toLowerCase());
            }
            if (beanFunctionContext.containsKey(autowired.value())
                    || beanFunctionContext.containsKey(type.getSimpleName().toLowerCase())) {
                BeanFunctionInterface fieldBeanFunction = beanFunctionContext.containsKey(autowired.value()) ?
                        beanFunctionContext.get(autowired.value()) :
                        beanFunctionContext.get(type.getSimpleName().toLowerCase());
                return fieldBeanFunction.getObject();
            }
            //全都失败了，说明代码有问题
            throw new BeanException("找不到" + type.getSimpleName() + "的Bean");
        }
        return beanContextByClass.containsKey(type) ?
                beanContextByClass.get(type) :
                beanContext.get(autowired.value());
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
        putBean(name, bean.getClass(), bean);
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
    public boolean beanAspect(Object object, Class<?> aspectClazz) {
        try {
            Method[] declaredMethods = aspectClazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                After after = declaredMethod.getAnnotation(After.class);
                Before before = declaredMethod.getAnnotation(Before.class);
                AfterThrowing afterThrowing = declaredMethod.getAnnotation(AfterThrowing.class);
                if (ObjectUtils.isNotNull(after)) {
                    if (!ObjectUtils.isStringEmpty(after.value())) {
                        String value = after.value();
                        if (methodMap.containsKey(value)) {
                            if (methodMap.get(value).containsKey(object)) {
                                methodMap.get(value).get(object).add(declaredMethod);
                            } else {
                                List<Method> list = new ArrayList<>();
                                list.add(declaredMethod);
                                methodMap.get(value).put(object, list);
                            }
//                        methodMap.get(value).put(object,declaredMethod);
                        } else {
                            ArrayList<Method> list = new ArrayList<>();
                            list.add(declaredMethod);
                            HashMap<Object, List<Method>> map = new HashMap<>();
                            map.put(object, list);
                            methodMap.put(value, map);
                        }
                    }

                }
                if (ObjectUtils.isNotNull(before)) {
                    if (!ObjectUtils.isStringEmpty(before.value())) {
                        String value = before.value();
                        if (methodMap.containsKey(value)) {
                            if (methodMap.get(value).containsKey(object)) {
                                methodMap.get(value).get(object).add(declaredMethod);
                            } else {
                                List<Method> list = new ArrayList<>();
                                list.add(declaredMethod);
                                methodMap.get(value).put(object, list);
                            }
//                        methodMap.get(value).put(object,declaredMethod);
                        } else {
                            ArrayList<Method> list = new ArrayList<>();
                            list.add(declaredMethod);
                            HashMap<Object, List<Method>> map = new HashMap<>();
                            map.put(object, list);
                            methodMap.put(value, map);
                        }
                    }
                }
                if (ObjectUtils.isNotNull(afterThrowing)) {
                    if (!ObjectUtils.isStringEmpty(afterThrowing.value())) {
                        String value = afterThrowing.value();
                        if (methodMap.containsKey(value)) {
                            if (methodMap.get(value).containsKey(object)) {
                                methodMap.get(value).get(object).add(declaredMethod);
                            } else {
                                List<Method> list = new ArrayList<>();
                                list.add(declaredMethod);
                                methodMap.get(value).put(object, list);
                            }
//                        methodMap.get(value).put(object,declaredMethod);
                        } else {
                            ArrayList<Method> list = new ArrayList<>();
                            list.add(declaredMethod);
                            HashMap<Object, List<Method>> map = new HashMap<>();
                            map.put(object, list);
                            methodMap.put(value, map);
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
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

    private boolean isNeedInitAspect(Class<?> clazz) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            Autowired methodAnnotation = declaredMethod.getAnnotation(Autowired.class);
            if (ObjectUtils.isNotNull(methodAnnotation)) {
                return true;
            }
            for (Parameter parameter : declaredMethod.getParameters()) {
                Autowired parameterAnnotation = parameter.getAnnotation(Autowired.class);
                if (ObjectUtils.isNotNull(parameterAnnotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNeedAspect(Class<?> clazz) {
        Set<String> listString = methodMap.keySet();
        String clazzName = clazz.getName();
        for (String name : listString) {
            if (clazzName.contains(name)) {
                return true;
            }
        }
        return false;

    }

    private void putBean(String name, Class<?> clazz, Object bean) {
        beanContext.put(name, bean);
        if (isPutBeanContextByClass(bean)) {
            beanContextByClass.put(clazz, bean);
        }
        First first = clazz.getAnnotation(First.class);
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            if (isPutBeanContextByClass(interfaceClazz) || ObjectUtils.isNotNull(first)) {
                beanContextByClass.put(interfaceClazz, bean);
            }
        }
    }

    private void putBeanFunction(String name, Class<?> clazz, BeanFunctionInterface beanFunctionInterface) {
        beanFunctionContext.put(name, beanFunctionInterface);
        beanFunctionContextByClass.put(clazz, beanFunctionInterface);
        First first = clazz.getAnnotation(First.class);
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            if (beanFunctionContextByClass.containsKey(interfaceClazz) || ObjectUtils.isNotNull(first)) {
                beanFunctionContextByClass.put(interfaceClazz, beanFunctionInterface);
            }
        }
    }
}

