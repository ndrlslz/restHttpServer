package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.handler.Handler;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.router.RouterContext;
import com.ndrlslz.router.RouterTable;

public class Test {
    public static void main(String[] args) {
        RouterTable routerTable = new RouterTable();

        routerTable.router("/").handler(context -> System.out.println(context.request().getUri()));


        RestHttpServer
                .create()
                .requestHandler(routerTable)
                .listen(8080);
    }
}
