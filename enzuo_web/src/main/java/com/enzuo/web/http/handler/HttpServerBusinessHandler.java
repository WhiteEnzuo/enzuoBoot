package com.enzuo.web.http.handler;

import cn.hutool.core.util.ObjectUtil;
import com.enzuo.web.http.annotation.WebServlet;
import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Classname HttpServerBusinessHandler
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 9:00
 * @Created by Enzuo
 */
@Slf4j
public class HttpServerBusinessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //通过编解码器把byteBuf解析成FullHttpRequest
        if (msg instanceof FullHttpRequest) {
            //获取httpRequest
            FullHttpRequest httpRequest = (FullHttpRequest) msg;

            try {
                Request request = Request.newInstance(httpRequest);
                String uri = request.getUri();
                HttpMethod method = request.getMethod();
                String content = request.getContent();
                log.info("服务器接收到请求:");
                log.info("请求uri:{},请求content:{},请求method:{}", uri, content, method);
                Response response = Response.newInstance();
                List<Filter> filterList = HttpContext.filterList;
                for (Filter filter : filterList) {
                    filter.doFilter(request, response);
                }
                List<HttpServlet> httpServletList = HttpContext.httpServletList;
                for (HttpServlet httpServlet : httpServletList) {
                    WebServlet webServlet = httpServlet.getClass().getAnnotation(WebServlet.class);
                    if (ObjectUtil.isNull(webServlet)) {
                        todoHttp(request, response, httpServlet);
                        continue;
                    }
                    String value = webServlet.value();
                    if (value.isEmpty()) {
                        todoHttp(request, response, httpServlet);
                        continue;
                    }
                    if (value.equals(uri)) {
                        todoHttp(request, response, httpServlet);
                        continue;
                    }
                    response.write(HttpContext.Response404);
                    response.setStatusCode(404);
                }
                ctx.writeAndFlush(response.getResponse()).addListener(ChannelFutureListener.CLOSE);
            } finally {
                httpRequest.release();
            }

        }
    }

    public void todoHttp(Request request, Response response, HttpServlet servlet) {
        HttpMethod method = request.getMethod();
        switch (method.name().toLowerCase(Locale.ROOT)) {
            case "get":
                servlet.doGet(request, response);
                break;
            case "post":
                servlet.doPost(request, response);
                break;
            case "delete":
                servlet.doDelete(request, response);
                break;
            case "header":
                servlet.doHeader(request, response);
                break;
            case "options":
                servlet.doOptions(request, response);
                break;
            case "put":
                servlet.doPut(request, response);
                break;
            case "trace":
                servlet.doTrace(request, response);
                break;
            default:
                servlet.doHttp(request, response);

        }
    }

}
