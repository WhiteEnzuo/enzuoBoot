package com.enzuo.mvc.servlet;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.mvc.context.ControllerContext;
import com.enzuo.mvc.interceptor.HandlerInterceptor;
import com.enzuo.mvc.interceptor.InterceptorRegistry;
import com.enzuo.mvc.interceptor.WebMvcConfigurerAdapter;
import com.enzuo.mvc.model.MethodObject;
import com.enzuo.mvc.model.ModelAndView;
import com.enzuo.mvc.model.impl.DefaultModelAndView;
import com.enzuo.web.http.annotation.WebServlet;
import com.enzuo.web.http.handler.HttpServlet;
import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname MVCServlet
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 16:41
 * @Created by Enzuo
 */
@WebServlet("/")
@Slf4j
public class MVCServlet extends HttpServlet {
    private ApplicationContext context;
    private ControllerContext controllerContext;
    private static final String MODEL_AND_VIEW_URL = "/MTF-INF/modelAndView.txt";
    private List<InterceptorRegistry> interceptorRegistryList = new ArrayList<>();
    private List<String> urlTemp = new ArrayList<>();

    public MVCServlet(ApplicationContext context) {
        this.context = context;
        this.controllerContext = new ControllerContext(context);
        List<WebMvcConfigurerAdapter> webMvcConfigurerAdapters = this.context.getBeanList(WebMvcConfigurerAdapter.class);
        for (WebMvcConfigurerAdapter webMvcConfigurerAdapter : webMvcConfigurerAdapters) {
            InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
            webMvcConfigurerAdapter.addInterceptors(interceptorRegistry);
            interceptorRegistryList.add(interceptorRegistry);
        }
    }

    private ModelAndView invokeModelAndView(Charset... encoders) {
        Charset encoder;
        if (encoders.length > 0) {
            encoder = encoders[0];
        } else {
            encoder = StandardCharsets.UTF_8;
        }
        Class<?> clazz = context.getClazz();
        InputStream resourceAsStream = clazz.getResourceAsStream(MODEL_AND_VIEW_URL);
        byte[] bytes;
        try {
            bytes = resourceAsStream.readAllBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            return new DefaultModelAndView();
        }

        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        String clazzName = byteBuf.toString(encoder);

        try {
            Class<?> objectClazz = Class.forName(clazzName);
            Constructor<?> declaredConstructor = objectClazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return (ModelAndView) declaredConstructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            return new DefaultModelAndView();

        }

    }

    @Override
    public void doGet(Request request, Response response) {
        String uri = request.getUri();
        HttpMethod httpMethod = request.getMethod();
        MethodObject methods = controllerContext.getMethods(httpMethod.name(), uri);
        Method method = methods.getMethod();
        List<Object> args = new ArrayList<>();
        method.setAccessible(true);
        ModelAndView modelAndView = invokeModelAndView();
        boolean flag = true;
        try {
            preHandle(request, response, null);
            Object invoke = method.invoke(methods.getObject(), args.toArray());
            if (invoke.getClass().equals(String.class)) {
                String html = modelAndView.readHtml((String) invoke);
                postHandle(request,response,null,modelAndView);
                response.write(html);
                afterCompletion(request,response,null,null);
                return;
            }
            flag = false;
            String json = modelAndView.readJson(invoke);
            response.write(json);
        } catch (Exception exception) {
            try {
                log.error(exception.getMessage());
                if (!flag) {
                    String html = modelAndView.errorHtml();
                    response.write(html);
                    afterCompletion(request,response,null,exception);
                    return;
                }
                String json = modelAndView.errorJson();
                response.write(json);
                afterCompletion(request,response,null,exception);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }


        }

    }

    private boolean preHandle(Request request, Response response, Object handler) throws Exception {
        boolean flag = canInterceptor(request.getUri());
        if (!flag) {
            return true;
        }
        for (InterceptorRegistry interceptorRegistry : this.interceptorRegistryList) {

            List<HandlerInterceptor> interceptors = interceptorRegistry.getInterceptors();
            for (HandlerInterceptor interceptor : interceptors) {
                if (!interceptor.preHandle(request, response, handler)) {
                    return false;
                }

            }

        }
        return true;
    }

    private boolean postHandle(Request request, Response response, Object handler, ModelAndView modelAndView) throws Exception {
        boolean flag = canInterceptor(request.getUri());
        if (!flag) {
            return true;
        }
        for (InterceptorRegistry interceptorRegistry : this.interceptorRegistryList) {

            List<HandlerInterceptor> interceptors = interceptorRegistry.getInterceptors();
            for (HandlerInterceptor interceptor : interceptors) {
                if (!interceptor.postHandle(request, response, handler, modelAndView)) {
                    return false;
                }

            }

        }
        return true;
    }

    private boolean afterCompletion(Request request, Response response, Object handler, Exception ex) throws Exception {
        boolean flag = canInterceptor(request.getUri());
        if (!flag) {
            return true;
        }
        for (InterceptorRegistry interceptorRegistry : this.interceptorRegistryList) {

            List<HandlerInterceptor> interceptors = interceptorRegistry.getInterceptors();
            for (HandlerInterceptor interceptor : interceptors) {
                if (!interceptor.afterCompletion(request, response, handler, ex)) {
                    return false;
                }

            }

        }
        return true;
    }

    private boolean canUrl(String targetUrl, String url) {
        if (urlTemp.contains(targetUrl)) {
            return true;
        }
        urlTemp.add(targetUrl);
        return true;
    }

    private boolean canInterceptor(String targetUrl) {
        boolean flag = false;
        for (InterceptorRegistry interceptorRegistry : this.interceptorRegistryList) {
            if (flag) {
                break;
            }
            for (String interceptorUrl : interceptorRegistry.getInterceptorUrls()) {
                if (canUrl(targetUrl, interceptorUrl)) {
                    flag = true;
                    break;
                }
            }

        }
        return flag;
    }

}
