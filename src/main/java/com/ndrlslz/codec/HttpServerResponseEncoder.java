package com.ndrlslz.codec;

import com.ndrlslz.model.HttpServerResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.List;

public class HttpServerResponseEncoder extends MessageToMessageDecoder<HttpServerResponse> {
    @Override
    protected void decode(ChannelHandlerContext ctx, HttpServerResponse msg, List<Object> out) {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(msg.getProtocolVersion(),
                HttpResponseStatus.valueOf(msg.getStatusCode()),
                Unpooled.copiedBuffer(msg.getBodyAsString(), CharsetUtil.UTF_8));

        out.add(defaultFullHttpResponse);

        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

    }
}
