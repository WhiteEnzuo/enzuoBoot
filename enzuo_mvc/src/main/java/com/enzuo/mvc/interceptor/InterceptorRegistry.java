package com.enzuo.mvc.interceptor;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname InterceptorRegistry
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 08:18
 * @Created by Enzuo
 */
@Getter
public class InterceptorRegistry {
    private List<String> interceptorUrls=new ArrayList<>();
    private List<HandlerInterceptor> interceptors = new ArrayList<>();
    public InterceptorRegistry addInterceptor(HandlerInterceptor handlerInterceptor){
        interceptors.add(handlerInterceptor);
        return this;
    }
    public InterceptorRegistry addPathPatterns(String url){
        interceptorUrls.add(url);
        return this;
    }
}
