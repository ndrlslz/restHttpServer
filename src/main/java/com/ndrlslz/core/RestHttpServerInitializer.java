package com.ndrlslz.core;

import com.ndrlslz.codec.HttpServerRequestDecoder;
import com.ndrlslz.codec.HttpServerResponseEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class RestHttpServerInitializer extends ChannelInitializer {
    private static final int MAX_CONTENT_LENGTH = 65536;

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        ch.pipeline().addLast(new HttpServerRequestDecoder());
        ch.pipeline().addLast(new HttpServerResponseEncoder());
        ch.pipeline().addLast(new RestHttpServerHandler());
    }
}
