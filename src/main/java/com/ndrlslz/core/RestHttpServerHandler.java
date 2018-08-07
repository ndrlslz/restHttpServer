package com.ndrlslz.core;

import com.ndrlslz.exception.RestHttpServerException;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.json.Json;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpServerResponseBuilder;
import com.ndrlslz.utils.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Objects;

import static com.ndrlslz.utils.ErrorBuilder.newBuilder;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class RestHttpServerHandler extends SimpleChannelInboundHandler<HttpServerRequest> {
    private static final Log LOG = LogFactory.getLog(RestHttpServerHandler.class);
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler;

    RestHttpServerHandler(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpServerRequest request) {
        if (HttpUtils.is100ContinueExpected(request)) {
            send100Continue(ctx, request);
        }

        if (Objects.isNull(requestHandler)) {
            throw new RestHttpServerException("Request handler cannot be null");
        }

        HttpServerResponse response = requestHandler.handle(request);

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Internal Server Error", cause);

        HttpServerResponse response = HttpServerResponseBuilder.internalServerError()
                .withBody(Json.encode(newBuilder()
                        .withException(cause.getClass().getName())
                        .withMessage(cause.getMessage())
                        .withStatus(INTERNAL_SERVER_ERROR.code())
                        .build()))
                .withStatusCode(INTERNAL_SERVER_ERROR.code())
                .withProtocolVersion(HttpVersion.HTTP_1_1)
                .build();

        ctx.writeAndFlush(response).addListener(CLOSE);
    }

    private static void send100Continue(ChannelHandlerContext ctx, HttpServerRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), CONTINUE);
        ctx.write(response);
    }
}
