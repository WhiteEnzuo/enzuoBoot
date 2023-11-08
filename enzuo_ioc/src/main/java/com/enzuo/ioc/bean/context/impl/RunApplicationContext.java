package com.enzuo.ioc.bean.context.impl;

import com.enzuo.ioc.bean.annotation.*;
import com.enzuo.ioc.bean.annotation.aop.Aspect;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.AfterBeanFactory;
import com.enzuo.ioc.bean.beanFactory.beanFactoryAspect.PostBeanFactory;
import com.enzuo.ioc.bean.beanFactory.impl.BeanFactory;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.expection.BeanException;
import com.enzuo.ioc.bean.utils.AnnotationUtils;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @Classname RunApplicationContext
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/4 19:12
 * @Created by Enzuo
 */
@Slf4j
public class RunApplicationContext extends ApplicationContext {
    private Class<?> clazz;
    private String[] args;
    private String className;
    private List<Class<?>> execute;
    private List<Class<?>> importClazz;
    private String clazzPath;
    private AbstractBeanFactory beanFactory;
    private static String fileSeparator = File.separator;
    private List<String> classNames = new ArrayList<>();
    private Map<String, Object> configMap;
    private final String configFile = "/application.yaml";

    private RunApplicationContext() {
        super();
        beanFactory = this.getBeanFactory();
    }

    public RunApplicationContext(Class<?> clazz, String[] args) {
        this();
        Application annotation = clazz.getAnnotation(Application.class);
        if (ObjectUtils.isNotNull(annotation)) {
            this.execute = new ArrayList<>(List.of(annotation.execute()));
            this.importClazz = new ArrayList<>(List.of(annotation.importClass()));
        }
        this.clazz = clazz;
        this.className = clazz.getName();
        this.getBeanFactory().registerSingletonBean(RunApplicationContext.class.getSimpleName(), this);
        this.args = args;
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
            sb.append(split[i]).append(fileSeparator);
        }
        if (sb.charAt(sb.length() - 1) == fileSeparator.toCharArray()[0]) {
            sb.deleteCharAt(sb.length() - 1);
        }
        this.clazzPath = sb.toString();
        String path = Objects.requireNonNull(clazz.getResource("")).getPath();
        initConfig();
        initBean(new File(path));
        initBeanByConfiguration();
        initImport();
        this.beanFactory.initBeanFactory(postBeanFactories, afterBeanFactories);
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    private void initConfig() {
        Yaml yaml = new Yaml();
        try {
            InputStream resourceAsStream = this.clazz.getResourceAsStream(configFile);
            this.configMap = (Map<String, Object>) yaml.loadAs(resourceAsStream, Map.class);
        } catch (Exception e) {
            this.configMap = new HashMap<>();
        }

    }

    @Override
    public void addImportClass(Class<?> clazz) {
        if (ObjectUtils.isNull(clazz) || ObjectUtils.isNull(this.importClazz)) {
            return;
        }
        this.importClazz.add(clazz);

    }

    @Override
    public void addExecuteImportClass(Class<?> clazz) {
        if (ObjectUtils.isNull(clazz) || ObjectUtils.isNull(this.execute)) {
            return;
        }
        this.execute.add(clazz);

    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public Object getConfig(String key) {
        if (ObjectUtils.isNull(this.configMap)) {
            this.configMap = new HashMap<>();
            return null;
        }
        String[] split = key.split("\\.");
        Object o = this.configMap.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            if(o instanceof Map){
                o=((Map<?, ?>) o).get(split[i]);
            }else {
                return null;
            }
        }
        return o;
    }

    @Override
    public void setConfig(String key, Object value) {
        if (ObjectUtils.isNull(this.configMap)) {
            this.configMap = new HashMap<>();
            return;
        }
        this.configMap.put(key, value);
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
                            .replace(fileSeparator, ".");


                    try {

                        Class<?> objClazz = Class.forName(className);
                        if(objClazz.isAnnotation()){
                            continue;
                        }
                        classNames.add(className);
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

                        if (ObjectUtils.isNull(component)||ObjectUtils.isStringEmpty(component.value())) {
                            beanFactory.registerBeanFunctionInterface(objClazz.getSimpleName(), objClazz);
                        } else {
                            beanFactory.registerBeanFunctionInterface(component.value(), objClazz);
                        }

                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                             InstantiationException | IllegalAccessException e) {
                        log.error(e.getMessage());
                    }

                }
            }
        }
    }

    public void initBeanByConfiguration() {
        for (String className : classNames) {
            try {

                Class<?> objClazz = Class.forName(className);
                if (!isConfiguration(objClazz)) {
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

            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }

    }

    private void initImport() {
        for (String className : classNames) {
            initImport(className);
        }
        if (ObjectUtils.isNotNull(this.importClazz)) {
            for (Class<?> clazz : this.importClazz) {
                if (this.execute.contains(clazz)) {

                    throw new BeanException("不能又导入又禁用");

                }
                invokeImportFunction(clazz);
            }
        }


    }

    private void initImport(String className) {
        try {

            Class<?> objClazz = Class.forName(className);
            if (!isImport(objClazz)) {
                return;
            }
            List<Import> imports = getImport(objClazz);
            for (Import importAnnotation : imports) {
                Class<?> value = importAnnotation.value();
                if ((ObjectUtils.isNotNull(this.execute) && this.execute.contains(value)) ||
                        (ObjectUtils.isNotNull(this.importClazz) && this.importClazz.contains(value))) {
                    continue;
                }

                invokeImportFunction(value);
            }

        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    private void invokeImportFunction(Class<?> clazz) {
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Object obj = declaredConstructor.newInstance();
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                Parameter[] parameters = declaredMethod.getParameters();
                Object invoke;
                if (parameters.length == 0) {
                    invoke = declaredMethod.invoke(obj);
                } else {
                    ArrayList<Object> parameterObjects = new ArrayList<>();
                    for (Parameter parameter : parameters) {
                        Object bean = beanFactory.getBean(parameter.getName());
                        parameterObjects.add(bean);
                    }
                    invoke = declaredMethod.invoke(obj, parameterObjects.toArray());
                }
                if (ObjectUtils.isNotNull(invoke)) {
                    beanFactory.registerSingletonBean(invoke.getClass().getSimpleName(), invoke);
                }
            }
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            log.error(e.getMessage());
        }

    }

    private boolean isComponent(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Component.class, null);
    }

    private boolean isConfiguration(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Configuation.class, null);
    }

    private boolean isImport(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Import.class, null);
    }

    private boolean isAspect(Class<?> clazz) {
        return AnnotationUtils.isAnnotation(clazz, Aspect.class, null);
    }

    private List<Import> getImport(Class<?> clazz) {
        Set<Import> importSet = new HashSet<>();
        for (Annotation annotation : clazz.getAnnotations()) {
            List<Import> imports = AnnotationUtils.getAnnotation(annotation.annotationType(), Import.class);
            importSet.addAll(imports);
        }
        return new ArrayList<>(importSet);
    }

}
