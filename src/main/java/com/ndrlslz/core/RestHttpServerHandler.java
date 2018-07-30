package com.ndrlslz.core;

import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.json.Json;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;

import static com.ndrlslz.utils.ErrorBuilder.newBuilder;
import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class RestHttpServerHandler extends SimpleChannelInboundHandler<HttpServerRequest> {
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler;

    RestHttpServerHandler(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpServerRequest request) {
        if (HttpUtils.is100ContinueExpected(request)) {
            send100Continue(ctx, request);
        }

        HttpServerResponse response = requestHandler.handle(request);

        ctx.writeAndFlush(response);
//
//        throw new RuntimeException("test");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //TODO this is not working currently if above throw exception. it cannot write result to client.
        cause.printStackTrace();
        ctx
                .writeAndFlush(copiedBuffer(Json.encode(newBuilder()
                        .withException(cause.getClass().getName())
                        .withMessage(cause.getMessage())
                        .withStatus(INTERNAL_SERVER_ERROR.code())
                        .build()), Charset.defaultCharset()))
                .addListener(CLOSE);
    }

    private static void send100Continue(ChannelHandlerContext ctx, HttpServerRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), CONTINUE);
        ctx.write(response);
    }
}
