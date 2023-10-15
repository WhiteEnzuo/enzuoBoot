package com.enzuo.ioc.bean.beanFactory.beanAspect;

import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.sun.xml.internal.ws.api.ha.HaInfo;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname BeanAspect
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/3 8:07
 * @Created by Enzuo
 */

public class BeanAspect implements MethodInterceptor {
    private Map<Method, String> map;
    private Method beforeMethod;
    private Object[] beforeMethodParameters;
    private Object beforeObject;
    private Method afterMethod;
    private Object[] afterMethodParameters;
    private Object afterObject;

    public BeanAspect(Map<Method, String> map,
                      Method beforeMethod,
                      Object[] beforeMethodParameters,
                      Object beforeObject, Method afterMethod,
                      Object[] afterMethodParameters,
                      Object afterObject) {
        this.map = map;
        this.beforeMethod = beforeMethod;
        this.beforeMethodParameters = beforeMethodParameters;
        this.beforeObject = beforeObject;
        this.afterMethod = afterMethod;
        this.afterMethodParameters = afterMethodParameters;
        this.afterObject = afterObject;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
            throws Throwable {
        if (method.getName().equals("toString")) {
            return o.getClass().getSimpleName();
        }

        if (map.containsKey(method)) {
            if (map.get(method).equals("before")) {
                if (ObjectUtils.isNotNull(beforeObject) || ObjectUtils.isNotNull(beforeMethodParameters)) {
                    beforeMethod.invoke(beforeObject, beforeMethodParameters);
                }
                return methodProxy.invokeSuper(o, objects);
            } else {
                Object invoke = methodProxy.invokeSuper(o, objects);
                if (ObjectUtils.isNotNull(afterObject) || ObjectUtils.isNotNull(afterMethod)) {
                    afterMethod.invoke(afterObject, afterMethodParameters);
                }
                return invoke;

            }
        }
        return methodProxy.invokeSuper(o, objects);
    }
}
