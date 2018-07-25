package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;

public class Router {
    private String path;
    private Handler<RouterContext> handler;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Handler<RouterContext> getHandler() {
        return handler;
    }

    public void setHandler(Handler<RouterContext> handler) {
        this.handler = handler;
    }
}
