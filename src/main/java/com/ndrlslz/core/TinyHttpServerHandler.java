package com.ndrlslz.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sun.deploy.net.HttpRequest.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static sun.tools.jconsole.Messages.CONNECTION;

public class TinyHttpServerHandler extends SimpleChannelInboundHandler {

    private static final String NEW_LINE = "\r\n";
    private HttpRequest request;
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            this.request = request;


            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            builder.append("Hello World").append(NEW_LINE);
            builder.append("Protocol Version: ").append(request.protocolVersion()).append(NEW_LINE);
            builder.append("Host: ").append(request.headers().get(HttpHeaderNames.HOST, "unknown")).append(NEW_LINE);
            builder.append("URI: ").append(request.uri()).append(NEW_LINE);
            builder.append("Method: ").append(request.method()).append(NEW_LINE);

            HttpHeaders headers = request.headers();
            headers.entries().forEach(entry ->
                    builder.append("Header ").append(entry.getKey()).append("=").append(entry.getValue()).append(NEW_LINE));

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            Map<String, List<String>> params = queryStringDecoder.parameters();

            if (!params.isEmpty()) {
                for (Map.Entry<String, List<String>> p: params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        builder.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                    }
                }
                builder.append("\r\n");
            }

        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            ByteBuf byteBuf = content.content();

            if (byteBuf.isReadable()) {
                builder.append("CONTENT: ").append(byteBuf.toString(Charset.defaultCharset())).append(NEW_LINE);
            }

        }

        if (msg instanceof LastHttpContent) {
            LastHttpContent lastHttpContent = (LastHttpContent) msg;

            if (!writeResponse(lastHttpContent, ctx)) {
                // If keep-alive is off, close the connection once the content is fully written.
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
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
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie : cookies) {
                    response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
            response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
        }

        // Write the response.
        ctx.write(response);

        return false;

    }
}
