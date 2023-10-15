package com.enzuo.mvc.servlet;

import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.mvc.context.ControllerContext;
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
    private static final String MODEL_AND_VIEW_URL="/MTF-INF/modelAndView.txt";


    public MVCServlet(ApplicationContext context) {
        this.context = context;
        this.controllerContext = new ControllerContext(context);
    }
    private ModelAndView invokeModelAndView(Charset... encoders){
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
        try {
            ModelAndView modelAndView = invokeModelAndView();
            Object invoke = method.invoke(methods.getObject(), args.toArray());
            if(invoke.getClass().equals(String.class)){
                String html = modelAndView.readHtml((String) invoke);
                response.write(html);
                return;
            }
            String json = modelAndView.readJson(invoke);
            response.write(json);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }

    }

}
