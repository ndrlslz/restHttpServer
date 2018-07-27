package com.ndrlslz.core;

import com.ndrlslz.codec.HttpServerRequestDecoder;
import com.ndrlslz.codec.HttpServerResponseEncoder;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class RestHttpServerInitializer extends ChannelInitializer {
    private static final int MAX_CONTENT_LENGTH = 65536;
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler;

    public RestHttpServerInitializer(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        pipeline.addLast(new HttpServerRequestDecoder());
        pipeline.addLast(new HttpServerResponseEncoder());
        pipeline.addLast(new RestHttpServerHandler(requestHandler));
    }
}
