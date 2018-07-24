package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;

public class Test {
    public static void main(String[] args) {
        RestHttpServer
                .create()
                .requestHandler(new RequestHandler<HttpServerRequest, HttpServerResponse>() {
                    @Override
                    public HttpServerResponse handle(HttpServerRequest request) {
                        return null;
                    }
                })
                .listen(8080);
    }
}
