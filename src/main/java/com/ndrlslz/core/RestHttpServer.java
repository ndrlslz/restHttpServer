package com.ndrlslz.core;

import com.ndrlslz.exception.TinyHttpServerException;
import com.ndrlslz.handler.Handler;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.AsyncResult;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RestHttpServer {
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestRequestHandler;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private RestHttpServer() {

    }

    public static RestHttpServer create() {
        return new RestHttpServer();
    }

    public RestHttpServer requestHandler(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestRequestHandler = requestHandler;
        return this;
    }

    public RestHttpServer listen(int port, Handler<AsyncResult<RestHttpServer>> handler) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        AsyncResult<RestHttpServer> result;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new RestHttpServerInitializer());

            b.bind(port).sync();

            result = AsyncResult.success(this);

        } catch (InterruptedException e) {
            TinyHttpServerException tinyHttpServerException = new TinyHttpServerException("Tiny http server startup fail.", e);
            result = AsyncResult.fail(tinyHttpServerException);
        }

        if (handler != null) {
            handler.handle(result);
        }

        return this;
    }

    public RestHttpServer listen(int port) {
        listen(port, null);
        return this;
    }

    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
