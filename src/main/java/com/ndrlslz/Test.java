package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.router.RouterTable;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;

public class Test {
    public static void main(String[] args) {
        RouterTable routerTable = new RouterTable();

        routerTable.router("/orders", HttpMethod.GET).handler(context -> {
            context.response().setBody("{\"name\": \"car\", \"price\" : 123}");
            context.response().headers().set(CONTENT_TYPE, APPLICATION_JSON.toString());
        });

        routerTable.router("/orders/{id}").handler(context -> {
            String id = context.request().getPathParams().get("id");
            context.response().setBody("{\"name\": \"car\", \"price\" : 123, \"id\": \"" + id + "\"}");
            context.response().headers().set(CONTENT_TYPE, APPLICATION_JSON.toString());
        });

        routerTable.router("/test").handler(context -> {
            HttpServerRequest request = context.request();
            String NEW_LINE = "\r\n";
            StringBuilder builder = new StringBuilder();

            builder.append("Hello World").append(NEW_LINE);
            builder.append("Protocol Version: ").append(request.getProtocolVersion()).append(NEW_LINE);
            builder.append("Host: ").append(request.headers().get("host")).append(NEW_LINE);
            builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
            builder.append("PATH: ").append(request.getPath()).append(NEW_LINE);
            builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
            builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);

            request.headers().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));

            request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));

            builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key")).append(NEW_LINE);

            builder.append("DecoderResult: ").append(request.decoderResult()).append(NEW_LINE);

            context.response().setBody(builder.toString());
        });


        RestHttpServer
                .create()
                .requestHandler(routerTable)
                .listen(8080);
    }
}
