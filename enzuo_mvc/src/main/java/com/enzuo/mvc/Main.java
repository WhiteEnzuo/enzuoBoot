package com.enzuo.mvc;

import com.enzuo.ioc.bean.annotation.Application;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.mvc.run.BootApplication;
import com.enzuo.web.http.handler.HttpContext;

/**
 * @Classname Main
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 16:42
 * @Created by Enzuo
 */

@Application
public class Main {
    public static void main(String[] args) {
        ApplicationContext run = BootApplication.run(Main.class, args);

    }

}
