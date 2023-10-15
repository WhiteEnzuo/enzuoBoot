package com.enzuo.mvc.model;

import com.alibaba.fastjson.JSON;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.mvc.model.result.Result;
import com.enzuo.web.http.handler.HttpContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Classname ModelAndView
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 18:55
 * @Created by Enzuo
 */
@Slf4j
public abstract class ModelAndView {
    public String readHtml(String path, Charset... encoders) {
        File file = new File(path);
        String html;
        Charset encoder;
        if (encoders.length > 0) {
            encoder = encoders[0];
        } else {
            encoder = StandardCharsets.UTF_8;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = fileInputStream.readAllBytes();

            ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
            html = byteBuf.toString(encoder);
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

}
