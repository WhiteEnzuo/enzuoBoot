package com.enzuo.web.http;

import com.enzuo.web.http.application.ServletApplicationRun;
import com.enzuo.web.http.server.HttpServerSocket;

/**
 * @Classname main
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 9:03
 * @Created by Enzuo
 */

public class Main {
    public static void main(String[] args) {
        HttpServerSocket httpServerSocket = new HttpServerSocket(8080);
        new ServletApplicationRun(Main.class, args, httpServerSocket).start();
    }
}
