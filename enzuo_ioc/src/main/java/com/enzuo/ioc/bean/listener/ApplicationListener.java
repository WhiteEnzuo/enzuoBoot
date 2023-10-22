package com.enzuo.ioc.bean.listener;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.env.Environment;

/**
 * @Classname ApplicationListener
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 16:39
 * @Created by Enzuo
 */
public interface ApplicationListener {
    default void starting() {
    }

    default void environmentPrepared(Environment environment) {
    }

    default void contextPrepared(ApplicationContext context) {
    }

    default void contextLoaded(ApplicationContext context) {
    }

    default void started(ApplicationContext context) {
    }

    default void running(ApplicationContext context) {
    }

    default void failed(ApplicationContext context, Throwable exception) {
    }

}
