package com.enzuo.web.http.handler;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname HttpContext
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/12 9:45
 * @Created by Enzuo
 */
public class HttpContext {
    public static final List<Filter> filterList = new ArrayList<>();
    public static final List<HttpServlet> httpServletList = new ArrayList<>();
    public static String Response404 = "<html><body><h1>404</h1></body></html>";
    public static String Response500 = "<html><body><h1>500</h1></body></html>";
}
