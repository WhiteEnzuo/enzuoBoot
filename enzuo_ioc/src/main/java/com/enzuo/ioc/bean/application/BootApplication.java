package com.enzuo.ioc.bean.application;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.context.impl.RunApplicationContext;
import com.enzuo.ioc.bean.env.Environment;
import com.enzuo.ioc.bean.listener.ApplicationListeners;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Classname BootApplication
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 16:33
 * @Created by Enzuo
 */
@Slf4j
public class BootApplication {
    private ApplicationContext applicationRun(Class<?> clazz,String[] args){

        Class<?>[] listener = this.getListener(clazz);

        RunApplicationContext runApplicationContext = null;
        runApplicationContext=new RunApplicationContext(clazz, args);

        ApplicationListeners applicationListeners = new ApplicationListeners(listener, runApplicationContext);
        applicationListeners.starting();
        Environment environment = new Environment();
        applicationListeners.environmentPrepared(environment);
        applicationListeners.contextPrepared(runApplicationContext);
        runApplicationContext.init(new ArrayList<>(),new ArrayList<>());
        applicationListeners.contextLoaded(runApplicationContext);
        applicationListeners.started(runApplicationContext);
        applicationListeners.running(runApplicationContext);

        return runApplicationContext;

    }
    private Class<?>[] getListener(Class<?> clazz)  {
        List<Class<?>> list = new ArrayList<>();

        try {
            InputStream resourceAsStream = clazz.getResourceAsStream(File.separator + "listener.txt");
            byte[] bytes = resourceAsStream.readAllBytes();
            String s = new String(bytes, Charset.defaultCharset());
            String[] classes = s.split(System.lineSeparator());
            for (String clazzString : classes) {

                Class<?> aClass = Class.forName(clazzString);
                list.add(aClass);


            }
        }catch (ClassNotFoundException | IOException e){
            log.error(e.getMessage());
        }
        return list.toArray(new Class<?>[]{});
    }
    public static ApplicationContext run(Class<?> clazz,String[] args){
        return new BootApplication().applicationRun(clazz,args);
    }
}
