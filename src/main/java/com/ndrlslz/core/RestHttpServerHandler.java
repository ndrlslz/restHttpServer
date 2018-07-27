package com.ndrlslz.core;

import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.CharsetUtil;

import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.cookie.ServerCookieDecoder.STRICT;
import static io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX;

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
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    private static void send100Continue(ChannelHandlerContext ctx, HttpServerRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), CONTINUE);
        ctx.write(response);
    }
}
