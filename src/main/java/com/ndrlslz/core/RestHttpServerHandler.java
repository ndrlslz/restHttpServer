package com.ndrlslz.core;

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

    private static final String NEW_LINE = "\r\n";
    private HttpRequest request;
    private StringBuilder builder = new StringBuilder();
    private HttpServerResponse response = new HttpServerResponse();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpServerRequest request) {
        if (HttpUtils.is100ContinueExpected(request)) {
            send100Continue(ctx, request);
        }

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

        response.setProtocolVersion(HTTP_1_1);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setStatusCode(OK.code());
        response.setBodyAsString(builder.toString());
        response.setDecoderResult(request.decoderResult());


        if (HttpUtils.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        }
        ctx.writeAndFlush(response);
    }

    private static void send100Continue(ChannelHandlerContext ctx, HttpServerRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), CONTINUE);
        ctx.write(response);
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = request.headers().get(COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie : cookies) {
                    response.headers().add(SET_COOKIE, LAX.encode(cookie));
                }

            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(SET_COOKIE, LAX.encode("key3", "value1"));
            response.headers().add(SET_COOKIE, LAX.encode("key3", "value2"));
        }
        response.headers().add("test", "456");
        response.headers().add("test", "4567");


        // Write the response.
        ctx.write(response);

        return false;
    }


}
