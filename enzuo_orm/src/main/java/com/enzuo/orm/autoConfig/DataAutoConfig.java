package com.enzuo.orm.autoConfig;

import com.enzuo.ioc.bean.annotation.Application;
import com.enzuo.ioc.bean.annotation.Component;
import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.utils.AnnotationUtils;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.orm.Test;
import com.enzuo.orm.annotation.Mapper;
import com.enzuo.orm.proxy.MapperProxyObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Classname DataAutoConfig
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/23 09:52
 * @Created by Enzuo
 */
@Slf4j
public class DataAutoConfig {
    private String clazzPath;
    private static String fileSeparator = File.separator;
    private AbstractBeanFactory beanFactory;
    public Connection load(ApplicationContext context) throws ClassNotFoundException, SQLException {
        String databaseDriver = (String)context.getConfig("database.driver");
        String databaseUrl = (String)context.getConfig("database.url");
        String databaseUserName = (String)context.getConfig("database.username");
        String databasePassword = (String)context.getConfig("database.password");
        Class.forName(databaseDriver);
        this.beanFactory=context.getBeanFactory();
        return DriverManager.getConnection(databaseUrl, databaseUserName, databasePassword);
    }
    public void loadMapper(ApplicationContext context){
        Class<?> clazz = context.getClazz();
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
                        if (!isMapper(objClazz)) {
                            continue;
                        }

                        Type[] genericInterfaces = objClazz.getGenericInterfaces();
                        if (genericInterfaces.length==0) {
                            continue;
                        }
                        String typeName = genericInterfaces[0].getTypeName();
                        String pattern = "(.*)<(.*)>";
                        Pattern compile = Pattern.compile(pattern);
                        Matcher matcher = compile.matcher(typeName);
                        String genericName = "";
                        if (matcher.find()) {
                            try {
                                genericName=matcher.group(2);
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        Object mapperObject = Proxy.newProxyInstance(
                                objClazz.getClassLoader(), new Class[]{objClazz},
                                new MapperProxyObject(Class.forName(genericName)));

                        beanFactory.registerSingletonBean(objClazz.getSimpleName(),mapperObject );


                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }

                }
            }
        }
    }

    private boolean isMapper(Class<?> clazz){
        return AnnotationUtils.isAnnotation(clazz, Mapper.class,null);
    }
}
