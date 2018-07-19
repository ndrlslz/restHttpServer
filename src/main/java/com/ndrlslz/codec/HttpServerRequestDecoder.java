package com.ndrlslz.codec;

import com.ndrlslz.model.HttpServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpMessage;

import java.util.List;

public class HttpServerRequestDecoder extends MessageToMessageDecoder<HttpMessage> {
    private HttpServerRequest httpServerRequest;

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpMessage msg, List<Object> out) {
    }
}
