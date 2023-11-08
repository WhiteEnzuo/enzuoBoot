package com.enzuo.orm.listener;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.listener.ApplicationListener;
import com.enzuo.orm.autoConfig.DataAutoConfig;

/**
 * @Classname OR
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/21 21:57
 * @Created by Enzuo
 */
public class ORMListener implements ApplicationListener {
    public ORMListener(ApplicationContext context){

    }
    @Override
    public void contextPrepared(ApplicationContext context) {
        context.addImportClass(DataAutoConfig.class);

    }
}
