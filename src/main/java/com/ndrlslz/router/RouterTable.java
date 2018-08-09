package com.ndrlslz.router;

import com.ndrlslz.exception.RestHttpServerException;
import com.ndrlslz.handler.Handler;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.json.Json;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpServerResponseBuilder;
import com.ndrlslz.utils.HttpUtils;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.ndrlslz.utils.ErrorBuilder.newBuilder;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
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
            httpServerResponse.headers().set(CONTENT_TYPE, APPLICATION_JSON.toString());
            httpServerResponse.setStatusCode(OK.code());
            httpServerResponse.setDecoderResult(context.request().decoderResult());
        });
    }

    public RouterTable router(String path) {
        Router router = new Router();
        router.setPath(path);
        currentRouter = router;

        if (isNull(currentRouter.getPath())) {
            globalRouters.add(currentRouter);
        } else {
            routers.add(currentRouter);
        }

        return this;
    }

    public RouterTable router(String path, HttpMethod method) {
        return router(path).method(method);
    }

    public RouterTable method(HttpMethod method) {
        Objects.requireNonNull(currentRouter, "please define router for this method");

        currentRouter.setHttpMethod(method);

        return this;
    }

    public RouterTable router() {
        return router(null);
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

    public RouterTable delete() {
        return router().method(HttpMethod.DELETE);
    }

    public RouterTable delete(String path) {
        return router(path).method(HttpMethod.DELETE);
    }

    public RouterTable put() {
        return router().method(HttpMethod.PUT);
    }

    public RouterTable put(String path) {
        return router(path).method(HttpMethod.PUT);
    }

    public RouterTable patch() {
        return router().method(HttpMethod.PATCH);
    }

    public RouterTable patch(String path) {
        return router(path).method(HttpMethod.PATCH);
    }

    public RouterTable handler(Handler<RouterContext> handler) {
        Objects.requireNonNull(currentRouter, "please define router for this handle");

        currentRouter.setHandler(handler);

        currentRouter = null;
        return this;
    }

    @Override
    public HttpServerResponse handle(HttpServerRequest request) {
        RouterContext routerContext = new RouterContext(request, new HttpServerResponse());

        globalRouters
                .stream()
                .filter(routerThatMatchMethodOf(request))
                .forEach(router -> router.getHandler().handle(routerContext));

        List<Router> matchedRouters = this.routers.stream()
                .filter(routerThatMatchMethodOf(request))
                .filter(routerThatMatchPathOf(request)).collect(Collectors.toList());

        if (matchedRouters.isEmpty()) {
            return HttpServerResponseBuilder.internalServerError()
                    .withRequest(request)
                    .withBody(Json.encode(newBuilder()
                            .withMessage("Cannot find available router")
                            .withStatus(INTERNAL_SERVER_ERROR.code())
                            .withUri(request.getUri())))
                    .build();
        }

        if (matchedRouters.size() > 1) {
            return HttpServerResponseBuilder.internalServerError()
                    .withRequest(request)
                    .withBody(Json.encode(newBuilder()
                            .withMessage("Find multiple routers")
                            .withStatus(INTERNAL_SERVER_ERROR.code())
                            .withUri(request.getUri())
                    )).build();
        }

        Router router = matchedRouters.get(0);

        if (isNull(router.getHandler())) {
            return HttpServerResponseBuilder.internalServerError()
                    .withRequest(request)
                    .withBody(Json.encode(newBuilder()
                            .withMessage("Cannot find available handle")
                            .withStatus(INTERNAL_SERVER_ERROR.code())
                            .withUri(request.getUri())
                    )).build();
        }
        Matcher matcher = router.getRegexPattern().matcher(request.getPath());

        if (matcher.find()) {
            try {
                routerContext.request().getPathParams().clear();
                for (int i = 0; i < router.getGroups().size(); i++) {
                    String key = router.getGroups().get(i);
                    routerContext.request().getPathParams().put(key, matcher.group("param" + i));
                }
            } catch (Exception exception) {
                throw new RestHttpServerException("Encounter error when retrieve path parameters", exception);
            }
        }

        router.getHandler().handle(routerContext);

        HttpServerResponse response = routerContext.response();

        if (HttpUtils.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.CLOSE.toString());
        }

        return response;
    }

    private Predicate<Router> routerThatMatchMethodOf(HttpServerRequest request) {
        return router -> router.getHttpMethod() == null || router.getHttpMethod().equals(request.getMethod());
    }

    private Predicate<Router> routerThatMatchPathOf(HttpServerRequest request) {
        return router -> router.getRegexPattern().matcher(request.getPath()).find();
    }
}
