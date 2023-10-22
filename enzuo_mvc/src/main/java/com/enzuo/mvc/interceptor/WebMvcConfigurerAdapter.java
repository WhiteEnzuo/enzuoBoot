package com.enzuo.mvc.interceptor;

/**
 * @Classname WebMvcConfigurerAdapter
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 08:16
 * @Created by Enzuo
 */
public abstract class WebMvcConfigurerAdapter {
    public abstract void addInterceptors(InterceptorRegistry registry);
}
