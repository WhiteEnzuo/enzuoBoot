package com.enzuo.web.http.handler.request;


import cn.hutool.core.util.ObjectUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Classname Request
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 9:22
 * @Created by Enzuo
 */
@Slf4j
@Data
public class Request {
    private FullHttpRequest request;
    private HttpHeaders headers;
    private String uri;
    private String content;
    private HttpMethod method;


    private Request() {
    }

    public static Request newInstance(FullHttpRequest request, Charset... encoders) {
        Charset encoder = StandardCharsets.UTF_8;
        if (ObjectUtil.isNotNull(encoders) && encoders.length > 0) {
            encoder = encoders[0];
        }
        Request obj = new Request();
        obj.request = request;
        obj.headers = request.headers();
        obj.uri = request.uri();
        obj.content = request.content().toString(encoder);
        obj.method = request.method();
        return obj;
    }
}
