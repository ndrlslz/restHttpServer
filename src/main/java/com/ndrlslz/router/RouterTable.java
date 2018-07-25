package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpUtils;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RouterTable implements RequestHandler<HttpServerRequest, HttpServerResponse> {
    private List<Router> routers = new ArrayList<>();
    private Router currentRouter;

    public RouterTable router(String path) {
        Router router = new Router();
        router.setPath(path);
        currentRouter = router;

        return this;
    }

    public RouterTable handler(Handler<RouterContext> handler) {
        currentRouter.setHandler(handler);

        routers.add(currentRouter);
        return this;
    }

    @Override
    public HttpServerResponse handle(HttpServerRequest request) {
        String NEW_LINE = "\r\n";
        StringBuilder builder = new StringBuilder();

        builder.append("Hello World").append(NEW_LINE);
        builder.append("Protocol Version: ").append(request.getProtocolVersion()).append(NEW_LINE);
        builder.append("Host: ").append(request.headers().get("host")).append(NEW_LINE);
        builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
        builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
        builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);

        request.headers().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));

        request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));

        builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key")).append(NEW_LINE);

        builder.append("DecoderResult: ").append(request.decoderResult()).append(NEW_LINE);

        HttpServerResponse response = new HttpServerResponse();
        response.setProtocolVersion(HTTP_1_1);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setStatusCode(OK.code());
        response.setBody(Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
        response.setDecoderResult(request.decoderResult());


        if (HttpUtils.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        }

        return response;
    }
}
