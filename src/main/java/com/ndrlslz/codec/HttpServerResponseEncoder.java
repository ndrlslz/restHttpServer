package com.ndrlslz.codec;

import com.ndrlslz.model.HttpServerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.Charset;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static java.util.Objects.nonNull;

public class HttpServerResponseEncoder extends MessageToMessageEncoder<HttpServerResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpServerResponse httpServerResponse, List<Object> out) {

        if (nonNull(httpServerResponse.decoderResult()) && httpServerResponse.decoderResult().isFailure()) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpServerResponse.getProtocolVersion(),
                    HttpResponseStatus.BAD_REQUEST,
                    Unpooled.copiedBuffer("Cannot deserializable request", Charset.defaultCharset()));

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        ByteBuf body = httpServerResponse.getBody() == null ?
                Unpooled.EMPTY_BUFFER :
                Unpooled.copiedBuffer(httpServerResponse.getBody(), Charset.defaultCharset());

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpServerResponse.getProtocolVersion(),
                HttpResponseStatus.valueOf(httpServerResponse.getStatusCode()),
                body);

        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        httpServerResponse.headers().each((key, value) -> response.headers().set(key, value));

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
