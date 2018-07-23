package com.ndrlslz.core;

import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import com.ndrlslz.utils.HttpUtils;
import com.sun.net.httpserver.HttpServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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
//    private HttpServerResponse response = new HttpServerResponse();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpServerRequest request) {
//        if (msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            this.request = request;
//
//
        if (HttpUtils.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }
//

        builder.append("Hello World").append(NEW_LINE);
        builder.append("Protocol Version: ").append(request.getProtocolVersion()).append(NEW_LINE);
        builder.append("Host: ").append(request.getHeaders().get("host")).append(NEW_LINE);
        builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
        builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
        builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);

        request.getHeaders().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));

        request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));

        builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key"));

//        response.setProtocolVersion(HTTP_1_1);
//        response.headers().put(CONTENT_TYPE, "text/plain; charset=UTF-8");
//        response.setStatusCode(OK.code());
//        response.setBodyAsString(builder.toString());


//        ctx.writeAndFlush(response);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));

        response.setProtocolVersion(request.getProtocolVersion());

        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        ctx.write(response);
//
//        if (request.getHeaders().get("connection") == null ||
//                !request.getHeaders().get("connection").contentEquals(HttpHeaderValues.KEEP_ALIVE)) {
//

        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        }
//
//        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//         Add keep alive header as per:
//         - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
//        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

//
//            HttpHeaders headers = request.headers();
//            headers.entries().forEach(entry ->
//                    builder.append("Header ").append(entry.getKey()).append("=").append(entry.getValue()).append(NEW_LINE));
//
//            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
//
//
//            Map<String, List<String>> params = queryStringDecoder.parameters();
//
//            if (!params.isEmpty()) {
//                for (Map.Entry<String, List<String>> p : params.entrySet()) {
//                    String key = p.getKey();
//                    List<String> vals = p.getValue();
//                    for (String val : vals) {
//                        builder.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
//                    }
//                }
//                builder.append("\r\n");
//            }
//
//        }
//
//        if (msg instanceof HttpContent) {
//            HttpContent content = (HttpContent) msg;
//
//            ByteBuf byteBuf = content.content();
//
//            if (byteBuf.isReadable()) {
//                builder.append("CONTENT: ").append(byteBuf.toString(Charset.defaultCharset())).append(NEW_LINE);
//            }
//
//        }
//
//        if (msg instanceof LastHttpContent) {
//            LastHttpContent lastHttpContent = (LastHttpContent) msg;
//
//            if (!writeResponse(lastHttpContent, ctx)) {
//
//
//                // If keep-alive is off, close the connection once the content is fully written.
//                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//            }
//        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
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
