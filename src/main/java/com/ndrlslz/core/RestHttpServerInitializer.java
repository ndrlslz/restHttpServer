package com.ndrlslz.core;

import com.ndrlslz.codec.HttpServerRequestDecoder;
import com.ndrlslz.codec.HttpServerResponseEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class RestHttpServerInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast(new HttpRequestDecoder());
        ch.pipeline().addLast(new HttpServerRequestDecoder());
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpServerResponseEncoder());
        ch.pipeline().addLast(new RestHttpServerHandler());
    }
}
