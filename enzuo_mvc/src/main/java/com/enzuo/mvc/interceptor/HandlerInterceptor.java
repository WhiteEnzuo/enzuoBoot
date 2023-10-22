package com.enzuo.mvc.interceptor;

import com.enzuo.mvc.model.ModelAndView;
import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;

/**
 * @Classname HandlerInterceptor
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 08:19
 * @Created by Enzuo
 */
public interface HandlerInterceptor {
    public boolean preHandle(Request request, Response response,Object handler) throws Exception;
    public boolean postHandle(Request request, Response response, Object handler, ModelAndView modelAndView) throws Exception;
    public boolean afterCompletion(Request request, Response response,Object handler,Exception ex) throws Exception;
}
