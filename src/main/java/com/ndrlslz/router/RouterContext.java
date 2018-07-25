package com.ndrlslz.router;

import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;

public class RouterContext {
    private HttpServerRequest request;
    private HttpServerResponse response;

    public RouterContext(HttpServerRequest request, HttpServerResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServerRequest request() {
        return request;
    }

    public void setRequest(HttpServerRequest request) {
        this.request = request;
    }

    public HttpServerResponse response() {
        return response;
    }

    public void setResponse(HttpServerResponse response) {
        this.response = response;
    }
}
