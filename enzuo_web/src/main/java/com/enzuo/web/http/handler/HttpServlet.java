package com.enzuo.web.http.handler;

import com.enzuo.web.http.handler.request.Request;
import com.enzuo.web.http.handler.request.Response;

/**
 * @Classname HttpServlet
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/12 9:40
 * @Created by Enzuo
 */

public abstract class HttpServlet {
    public abstract void doGet(Request request, Response response);

    public void doPost(Request request, Response response) {
        this.doGet(request, response);

    }

    public void doPut(Request request, Response response) {
        this.doGet(request, response);
    }

    public void doOptions(Request request, Response response) {
        this.doGet(request, response);
    }

    public void doDelete(Request request, Response response) {
        this.doGet(request, response);
    }

    public void doTrace(Request request, Response response) {
        this.doGet(request, response);
    }

    public void connect(Request request, Response response) {
        this.doGet(request, response);
    }

    public void doHeader(Request request, Response response) {
        this.doGet(request, response);
    }

    public void doHttp(Request request, Response response) {
        this.doGet(request, response);
    }
}
