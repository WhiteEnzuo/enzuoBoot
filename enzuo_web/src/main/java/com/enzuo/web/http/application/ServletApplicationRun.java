package com.enzuo.web.http.application;

import com.enzuo.web.http.handler.Filter;
import com.enzuo.web.http.handler.HttpContext;
import com.enzuo.web.http.handler.HttpServlet;
import com.enzuo.web.http.server.HttpServerSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * @Classname ServletApplicationRun
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/12 10:02
 * @Created by Enzuo
 */
@Slf4j
public class ServletApplicationRun {
    private Class<?> clazz;
    private String[] args;
    private HttpServerSocket httpServerSocket;
    private final String WEB_ServLet_CONFIG = "/WEB-APP/Servlet/servlet.txt";
    private final String WEB_Filter_CONFIG = "/WEB-APP/Filter/filter.txt";

    public ServletApplicationRun(Class<?> clazz, String[] args, HttpServerSocket httpServerSocket) {
        this.clazz = clazz;
        this.args = args;
        this.httpServerSocket = httpServerSocket;
    }

    public void start() {
        registerHttpServlet();
        registerFilter();
        httpServerSocket.start();
    }

    private void registerHttpServlet() {
        String path = Objects.requireNonNull(clazz.getResource(WEB_ServLet_CONFIG)).getPath();
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            if (bytes.length == 0) return;
            inputStream.read(bytes);
            String[] objectClazzStrings = new String(bytes).replace(System.lineSeparator(), "").split("\\n");
            for (String objClazzString : objectClazzStrings) {
                Class<?> objectClazz = Class.forName(objClazzString);
                Constructor<?> declaredConstructor = objectClazz.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                Object instance = declaredConstructor.newInstance();
                HttpContext.httpServletList.add((HttpServlet) instance);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(0);
        }
    }

    private void registerFilter() {
        String path = Objects.requireNonNull(clazz.getResource(WEB_Filter_CONFIG)).getPath();
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            if (bytes.length == 0) return;
            inputStream.read(bytes);
            String[] objectClazzStrings = new String(bytes).replace(System.lineSeparator(), "").split("\\n");
            for (String objClazzString : objectClazzStrings) {
                Class<?> objectClazz = Class.forName(objClazzString);
                Constructor<?> declaredConstructor = objectClazz.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                Object instance = declaredConstructor.newInstance();
                HttpContext.filterList.add((Filter) instance);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(0);
        }
    }

}
