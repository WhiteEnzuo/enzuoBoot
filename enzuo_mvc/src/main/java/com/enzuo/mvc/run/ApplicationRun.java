package com.enzuo.mvc.run;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.context.impl.RunApplicationContext;
import com.enzuo.mvc.Main;
import com.enzuo.mvc.servlet.MVCServlet;
import com.enzuo.web.http.application.ServletApplicationRun;
import com.enzuo.web.http.handler.HttpContext;
import com.enzuo.web.http.server.HttpServerSocket;

import java.util.ArrayList;

/**
 * @Classname ApplicationRun
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 16:44
 * @Created by Enzuo
 */
public class ApplicationRun {
    public static ApplicationContext run(Class<?> clazz,String [] args){
        ApplicationContext context=new RunApplicationContext(clazz,args);
        context.init(new ArrayList<>(),new ArrayList<>());
        MVCServlet mvcServlet = new MVCServlet(context);
        context.getBeanFactory().registerSingletonBean("mvcServlet",mvcServlet);
        new Thread(()->{
            HttpServerSocket httpServerSocket = new HttpServerSocket(8080);
            HttpContext.httpServletList.add(mvcServlet);
            new ServletApplicationRun(Main.class, args, httpServerSocket).start();
        }).start();
        return context;
    }
}
