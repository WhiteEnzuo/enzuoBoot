package com.enzuo.mvc.model;

import com.alibaba.fastjson.JSON;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.mvc.model.result.Result;
import com.enzuo.mvc.redirect.RedirectHtml;
import com.enzuo.web.http.handler.HttpContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Classname ModelAndView
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 18:55
 * @Created by Enzuo
 */
@Slf4j
public abstract class ModelAndView {

    public String readHtml(Class<?> clazz, RedirectHtml redirectHtml, Charset... encoders) {
        String html;
        Charset encoder;
        if (encoders.length > 0) {
            encoder = encoders[0];
        } else {
            encoder = StandardCharsets.UTF_8;
        }

        try {
            InputStream htmlStream = clazz.getResourceAsStream(File.separator + redirectHtml.getRedirectHtmlPath());
            byte[] bytes = htmlStream.readAllBytes();
            ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
            html = byteBuf.toString(encoder);
            html=makeHtml(redirectHtml.getData(),html);

        } catch (IOException e) {
            log.error(e.getMessage());
            html = notFoundHtml(encoder);
        }
        return html;
    }

    public String notFoundHtml(Charset... encoders) {
        String response404 = HttpContext.Response404;
        ByteBuf byteBuf = Unpooled.copiedBuffer(response404.getBytes());
        if (encoders.length > 0) {

            return byteBuf.toString(encoders[0]);
        }
        return byteBuf.toString(StandardCharsets.UTF_8);
    }
    public String errorHtml(Charset... encoders){
        String response404 = HttpContext.Response500;
        ByteBuf byteBuf = Unpooled.copiedBuffer(response404.getBytes());
        if (encoders.length > 0) {

            return byteBuf.toString(encoders[0]);
        }
        return byteBuf.toString(StandardCharsets.UTF_8);
    }

    public String notFoundJson() {
        Result result = Result.notFound();
        Object json = JSON.toJSON(result);
        return json.toString();
    }

    public String readJson(Object object) {
        if(ObjectUtils.isNull(object)){
            return errorJson();
        }
        return JSON.toJSON(object).toString();
    }
    public String errorJson() {
        Result result = Result.error();
        Object json = JSON.toJSON(result);
        return json.toString();
    }
    private String makeHtml(Map<String ,Object> data,String html){
        for (String key : data.keySet()) {
            html = html.replace("{{"+key+"}}",data.get(key).toString());
        }
        return html;
    }
}
