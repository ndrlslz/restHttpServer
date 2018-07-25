package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpUtils;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.util.Objects.isNull;

public class RouterTable implements RequestHandler<HttpServerRequest, HttpServerResponse> {
    private List<Router> routers;
    private List<Router> globalRouters;
    private Router currentRouter;

    public RouterTable() {
        routers = new ArrayList<>();
        globalRouters = new ArrayList<>();

        router().handler(context -> {
            HttpServerRequest httpServerRequest = context.request();
            HttpServerResponse httpServerResponse = context.response();
            httpServerResponse.setProtocolVersion(httpServerRequest.getProtocolVersion());
            httpServerResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            httpServerResponse.setStatusCode(OK.code());
//            httpServerResponse.setBody(Unpooled.EMPTY_BUFFER);

        });
    }

    public RouterTable router(String path) {
        Router router = new Router();
        router.setPath(path);
        currentRouter = router;

        return this;
    }

    public RouterTable router(String path, HttpMethod method) {
        return router(path).method(method);
    }

    public RouterTable method(HttpMethod method) {
        currentRouter.setHttpMethod(method);

        return this;
    }

    public RouterTable router() {
        currentRouter = new Router();

        return this;
    }

    public RouterTable get() {
        return router().method(HttpMethod.GET);
    }

    public RouterTable get(String path) {
        return router(path, HttpMethod.GET);
    }

    public RouterTable post() {
        return router().method(HttpMethod.POST);
    }

    public RouterTable post(String path) {
        return router(path, HttpMethod.POST);
    }

    public RouterTable handler(Handler<RouterContext> handler) {
        Objects.requireNonNull(currentRouter, "please define router for this handle");

        currentRouter.setHandler(handler);

        if (isNull(currentRouter.getPath())) {
            globalRouters.add(currentRouter);
        } else {
            routers.add(currentRouter);
        }

        currentRouter = null;
        return this;
    }

    @Override
    public HttpServerResponse handle(HttpServerRequest request) {
        //        httpServerResponse.setProtocolVersion(HTTP_1_1);
//        httpServerResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
//        httpServerResponse.setStatusCode(OK.code());
//        httpServerResponse.setBody(Unpooled.EMPTY_BUFFER);

        RouterContext routerContext = new RouterContext(request, new HttpServerResponse());

        globalRouters.forEach(router -> router.getHandler().handle(routerContext));

        List<Router> matchedRouters = this.routers.stream()
                .filter(routerThatMatchMethodOf(request))
                .filter(routerThatMatchPathOf(request)).collect(Collectors.toList());

        if (matchedRouters.isEmpty()) {
            HttpServerResponse exceptionResponse = new HttpServerResponse();
            exceptionResponse.setStatusCode(500);
            exceptionResponse.setProtocolVersion(request.getProtocolVersion());
            //TODO return json string error
            exceptionResponse.setBody("Cannot find available router");
            return exceptionResponse;
        }

        matchedRouters.forEach(router -> router.getHandler().handle(routerContext));

//        String NEW_LINE = "\r\n";
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Hello World").append(NEW_LINE);
//        builder.append("Protocol Version: ").append(request.getProtocolVersion()).append(NEW_LINE);
//        builder.append("Host: ").append(request.headers().get("host")).append(NEW_LINE);
//        builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
//        builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
//        builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);
//
//        request.headers().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));
//
//        request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));
//
//        builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key")).append(NEW_LINE);
//
//        builder.append("DecoderResult: ").append(request.decoderResult()).append(NEW_LINE);

        HttpServerResponse response = routerContext.response();
//        response.setProtocolVersion(HTTP_1_1);
//        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
//        response.setStatusCode(OK.code());
//        response.setBody(Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
//        response.setDecoderResult(request.decoderResult());


        if (HttpUtils.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        }

        return response;
    }

    private Predicate<Router> routerThatMatchMethodOf(HttpServerRequest request) {
        return router -> router.getHttpMethod() == null || router.getHttpMethod().equals(request.getMethod());
    }

    private Predicate<Router> routerThatMatchPathOf(HttpServerRequest request) {
        return router -> router.getPath().equals(request.getPath());
    }
}
