package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;
import io.netty.handler.codec.http.HttpMethod;

public class Router {
    private String path;
    private HttpMethod httpMethod;
    private Handler<RouterContext> handler;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Handler<RouterContext> getHandler() {
        return handler;
    }

    public void setHandler(Handler<RouterContext> handler) {
        this.handler = handler;
    }
}
