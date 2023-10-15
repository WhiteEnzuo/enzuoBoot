package com.enzuo.web.http.handler;

import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;

/**
 * @interfaceName Filter
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 9:18
 * @Created by Enzuo
 */

public interface Filter {
//    public void init();
    public void doFilter(Request request, Response response);
//    public void destroy();
}
