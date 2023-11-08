package com.enzuo.mvc.run;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.context.impl.RunApplicationContext;
import com.enzuo.ioc.bean.env.Environment;
import com.enzuo.ioc.bean.listener.ApplicationListeners;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.mvc.servlet.MVCServlet;
import com.enzuo.web.http.handler.HttpContext;
import com.enzuo.web.http.server.HttpServerSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    private static final List<Class<?>> listenerList = new ArrayList<>();
    private RunApplicationContext runApplicationContext;
    private HttpServerSocket httpServerSocket;

    private ApplicationContext applicationRun(Class<?> clazz, String[] args) {


        Class<?>[] listener = this.getListener(clazz);
        runApplicationContext = new RunApplicationContext(clazz, args);
        ApplicationListeners applicationListeners = new ApplicationListeners(listener, runApplicationContext);
        applicationListeners.starting();
        Environment environment = new Environment();
        environmentPrepared(applicationListeners,environment);
        applicationListeners.contextPrepared(runApplicationContext);
        runApplicationContext.init(new ArrayList<>(), new ArrayList<>());
        applicationListeners.contextLoaded(runApplicationContext);
        applicationListeners.started(runApplicationContext);
        runServer(applicationListeners);
        return runApplicationContext;

    }

    private Class<?>[] getListener(Class<?> clazz) {

        try {
            InputStream resourceAsStream = clazz.getResourceAsStream(File.separator + "listener.txt");
            byte[] bytes = resourceAsStream.readAllBytes();
            String s = new String(bytes, Charset.defaultCharset());
            String[] classes = s.split(System.lineSeparator());
            for (String clazzString : classes) {
                Class<?> aClass = Class.forName(clazzString);
                if (listenerList.contains(aClass)) {
                    continue;
                }
                registerListener(aClass);
            }
        } catch (ClassNotFoundException | IOException e) {
            log.error(e.getMessage());
        }
        return listenerList.toArray(new Class<?>[]{});
    }

    public static boolean registerListener(Class<?> clazz) {
        return listenerList.add(clazz);
    }

    public static ApplicationContext run(Class<?> clazz, String[] args) {
        return new BootApplication().applicationRun(clazz, args);
    }

    private void runServer(ApplicationListeners applicationListeners) {

        new Thread(() -> {
            if (ObjectUtils.isNull(httpServerSocket)) {
                httpServerSocket = new HttpServerSocket(8080);
            }
            httpServerSocket.start();
        }).start();
        applicationListeners.running(runApplicationContext);

    }
    private void environmentPrepared(ApplicationListeners applicationListeners,Environment environment){
        applicationListeners.environmentPrepared(environment);
        MVCServlet mvcServlet = new MVCServlet(runApplicationContext);
        runApplicationContext.getBeanFactory().registerSingletonBean("mvcServlet",mvcServlet);
        HttpContext.httpServletList.add(mvcServlet);
        switch (environment.getHttpContext()){
            case "Servlet":
                int port = environment.getPort();
                String address = environment.getAddress();
                httpServerSocket=new HttpServerSocket(address,port);
                break;
            default:
                httpServerSocket=new HttpServerSocket(8080);
        }

    }
}
