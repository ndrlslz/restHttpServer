package com.ndrlslz.core;

import com.ndrlslz.codec.HttpServerRequestDecoder;
import com.ndrlslz.codec.HttpServerResponseEncoder;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class RestHttpServerInitializer extends ChannelInitializer {
    private static final int MAX_CONTENT_LENGTH = 65536;
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler;

    RestHttpServerInitializer(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast(new ReadTimeoutHandler(30))
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH))
                .addLast(new HttpServerRequestDecoder())
                .addLast(new HttpServerResponseEncoder()).addLast(new RestHttpServerHandler(requestHandler));
    }
}
