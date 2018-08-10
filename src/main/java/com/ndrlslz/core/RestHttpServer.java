package com.ndrlslz.core;

import com.ndrlslz.exception.RestHttpServerException;
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
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import static java.util.Objects.nonNull;

public class RestHttpServer {
    private RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private RestHttpServerOptions restHttpServerOptions;
    private static EventExecutorGroup requestHandlerGroup;

    private RestHttpServer() {

    }

    public static RestHttpServer create() {
        return new RestHttpServer();
    }

    public static RestHttpServer create(RestHttpServerOptions options) {
        RestHttpServer restHttpServer = create();
        restHttpServer.restHttpServerOptions = options;
        return restHttpServer;
    }

    public RestHttpServer requestHandler(RequestHandler<HttpServerRequest, HttpServerResponse> requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    public RestHttpServer listen(int port, Handler<AsyncResult<RestHttpServer>> handler) {
        if (nonNull(restHttpServerOptions) && nonNull(restHttpServerOptions.getWorkerThreadCount())) {
            requestHandlerGroup = new DefaultEventExecutorGroup(restHttpServerOptions.getWorkerThreadCount());
        }

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        AsyncResult<RestHttpServer> result;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new RestHttpServerInitializer(requestHandler, requestHandlerGroup));

            b.bind(port).sync();

            result = AsyncResult.success(this);

        } catch (InterruptedException e) {
            RestHttpServerException restHttpServerException = new RestHttpServerException("Rest http server startup fail.", e);
            result = AsyncResult.fail(restHttpServerException);
        }

        if (handler != null) {
            handler.handle(result);
        }

        return this;
    }

    public RestHttpServer listen(int port) {
        return listen(port, null);
    }

    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
