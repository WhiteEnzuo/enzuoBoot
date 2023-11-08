//package com.enzuo.mvc.application;
//
//import com.enzuo.ioc.bean.context.ApplicationContext;
//import com.enzuo.ioc.bean.listener.ApplicationListener;
//import com.enzuo.web.http.server.HttpServerSocket;
//
///**
// * @Classname RunBootApplication
// * @Description
// * @Version 1.0.0
// * @Date 2023/11/06 11:28
// * @Created by Enzuo
// */
//public class RunBootApplicationListener implements ApplicationListener {
//    public RunBootApplicationListener(ApplicationContext context){}
//    public void running(ApplicationContext context) {
//        new Thread(()->{HttpServerSocket httpServerSocket = new HttpServerSocket(8080);
//            httpServerSocket.start();}).start();
//
//    }
//}
