package com.ndrlslz.codec;

import com.ndrlslz.model.HttpServerResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

public class HttpServerResponseEncoder extends MessageToMessageEncoder<HttpServerResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpServerResponse httpServerResponse, List<Object> out) {

        //TODO handle null exception if getBody() return null
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpServerResponse.getProtocolVersion(),
                HttpResponseStatus.valueOf(httpServerResponse.getStatusCode()),
                Unpooled.copiedBuffer(httpServerResponse.getBody(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        httpServerResponse.headers().each((key, value) -> response.headers().set(key, value));

        ctx.write(response);

//        if (!HttpUtils.isKeepAlive(httpServerResponse)) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
