package com.enzuo.ioc.bean.context.impl;

import com.enzuo.ioc.bean.annotation.Bean;
import com.enzuo.ioc.bean.annotation.Component;
import com.enzuo.ioc.bean.annotation.Configuation;
import com.enzuo.ioc.bean.annotation.aop.Aspect;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.PostBeanFactory;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.utils.AnnotationUtils;
import com.enzuo.ioc.bean.utils.ObjectUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Classname RunApplicationContext
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/4 19:12
 * @Created by Enzuo
 */

public class RunApplicationContext extends ApplicationContext {
    private Class<?> clazz;
    private String className;
    private String clazzPath;
    private AbstractBeanFactory beanFactory;

    private RunApplicationContext() {
        super();
        beanFactory = this.getBeanFactory();
    }

    public RunApplicationContext(Class<?> clazz) {
        this();
        this.clazz = clazz;
        this.className = clazz.getName();
        this.getBeanFactory().registerSingletonBean(RunApplicationContext.class.getSimpleName(), this);
        this.init(new ArrayList<>(), new ArrayList<>());
    }


    @Override
    public Object getBean(String beanName) {
        if (ObjectUtils.isNull(getBeanFactory())) {
            super.setBeanFactory(new BeanFactory());
            return null;
        }
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        if (ObjectUtils.isNull(getBeanFactory())) {
            super.setBeanFactory(new BeanFactory());
            return null;
        }
        return getBeanFactory().getBean(beanName, clazz);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (ObjectUtils.isNull(getBeanFactory())) {
            super.setBeanFactory(new BeanFactory());
            return null;
        }
        return getBeanFactory().getBean(clazz);
    }

    @Override
    public <T> Map<String, T> getBeanMap(Class<T> clazz) {
        if (ObjectUtils.isNull(getBeanFactory())) {
            super.setBeanFactory(new BeanFactory());
            return new HashMap<>();
        }
        return getBeanFactory().getBeanMap(clazz);
    }

    @Override
    public <T> List<T> getBeanList(Class<T> clazz) {
        if (ObjectUtils.isNull(getBeanFactory())) {
            super.setBeanFactory(new BeanFactory());
            return new ArrayList<>();
        }
        return getBeanFactory().getBeanList(clazz);
    }

    @Override
    public void init(List<PostBeanFactory> postBeanFactories, List<AfterBeanFactory> afterBeanFactories) {

        String[] split = clazz.getName().split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]).append("\\");
        }
        if (sb.charAt(sb.length() - 1) == '\\') {
            sb.deleteCharAt(sb.length() - 1);
        }
        this.clazzPath = sb.toString();
        String path = Objects.requireNonNull(clazz.getResource("")).getPath();
        initBean(new File(path));
        initBeanByConfiguation(new File(path));
        this.beanFactory.initBeanFactory(postBeanFactories, afterBeanFactories);
    }

    private void initBean(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File pathFile : files) {
                if (pathFile.isDirectory()) {
                    initBean(pathFile);
                    continue;
                }
                if (pathFile.getName().contains(".class")) {

                    String className = pathFile.getPath()
                            .substring(pathFile.getPath().indexOf(this.clazzPath))
                            .replace(".class", "")
                            .replace("\\", ".");
                    try {

                        Class<?> objClazz = Class.forName(className);
                        if (!isComponent(objClazz)) {
                            continue;
                        }
                        if (isAspect(objClazz)) {
                            Constructor<?> declaredConstructor = objClazz.getDeclaredConstructor();
                            declaredConstructor.setAccessible(true);
                            Object instance = declaredConstructor.newInstance();
                            beanFactory.beanAspect(instance, objClazz);
                        }
                        Component component = objClazz.getAnnotation(Component.class);

                        if (ObjectUtils.isStringEmpty(component.value())) {
                            beanFactory.registerBeanFunctionInterface(objClazz.getSimpleName(), objClazz);
                        } else {
                            beanFactory.registerBeanFunctionInterface(component.value(), objClazz);
                        }

                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void initBeanByConfiguation(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File pathFile : files) {
                if (pathFile.isDirectory()) {
                    initBeanByConfiguation(pathFile);
                    continue;
                }
                if (pathFile.getName().contains(".class")) {
                    String className = pathFile.getPath().substring(pathFile.getPath().indexOf(this.clazzPath))
                            .replace(".class", "").replace("\\", ".");
                    try {

                        Class<?> objClazz = Class.forName(className);
                        if (!isConfiguation(objClazz)) {
                            continue;
                        }
                        Object instance = objClazz.getDeclaredConstructor().newInstance();

                        for (Method declaredMethod : objClazz.getDeclaredMethods()) {
                            Bean bean = declaredMethod.getAnnotation(Bean.class);
                            if (ObjectUtils.isNull(bean)) {
                                continue;
                            }
                            List<Object> params = new ArrayList<>();
                            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();

                            for (int i = 0; i < parameterTypes.length; i++) {
                                params.add(i, beanFactory.getBean(parameterTypes[i]));
                            }
                            Object invoke = declaredMethod.invoke(instance, params.toArray());
                            if (ObjectUtils.isNull(invoke)) {
                                continue;
                            }
                            beanFactory.registerSingletonBean(
                                    ObjectUtils.isStringEmpty(bean.value()) ?
                                            invoke.getClass().getSimpleName() : bean.value()
                                    , invoke);

                        }

                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private boolean isComponent(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Component.class, null);
    }

    private boolean isConfiguation(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Configuation.class, null);
    }

    private boolean isAspect(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Aspect.class, null);
    }

}
