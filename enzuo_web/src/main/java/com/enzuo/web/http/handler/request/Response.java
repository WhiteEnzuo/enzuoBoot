package com.enzuo.web.http.handler.request;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.apache.http.HttpStatus;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Classname Response
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 9:22
 * @Created by Enzuo
 */
@Data
public class Response {
    private FullHttpResponse response;

    private Response() {
    }

    public static Response newInstance() {
        Response response = new Response();
        response.response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK
        );

        return response;
    }

    public void write(String msg, Charset... encoders) {
        Charset encoder = StandardCharsets.UTF_8;
        if (ObjectUtil.isNotNull(encoder) && encoders.length > 0) {
            encoder = encoders[0];
        }
        response=response.replace(Unpooled.copiedBuffer(msg, encoder));
    }

    public void write(ByteBuf msg) {
        response=response.replace(msg);
    }

    public void setHeader(Map<String, Object> header) {
        HttpHeaders headers = new DefaultHttpHeaders();
        header.forEach(headers::set);
        response.headers().set(headers);
    }

    public void setHeaderValue(String key, Object value) {
        response.headers().set(key, value);
    }

    public void addHeaderValue(String key, Object value) {
        response.headers().add(key, value);
    }

    public void removeHeaderValue(String key) {
        response.headers().remove(key);
    }

    public Object getHeaderValue(String key) {
        if (!response.headers().contains(key)) {
            return null;
        }
        return response.headers().get(key);
    }

    public HttpHeaders getHeader(String key) {
        return response.headers();
    }
    public void setStatusCode(int code){
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(code);
        response.setStatus(httpResponseStatus);
    }

}
