package com.enzuo.web.http.handler.impl;

import com.enzuo.web.http.annotation.WebServlet;
import com.enzuo.web.http.handler.HttpServlet;
import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;

/**
 * @Classname DefalutHttpServlet
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/12 10:15
 * @Created by Enzuo
 */
@WebServlet("/test")
public class DefaultHttpServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        response.write("test");
    }
}
