package com.ndrlslz.core;

import com.ndrlslz.handler.Handler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TinyHttpServer {
    private Handler<HttpServerRequest, HttpServerResponse> requestHandler;

    private TinyHttpServer() {

    }

    public static TinyHttpServer create() {
        return new TinyHttpServer();
    }

    public TinyHttpServer requestHandler(Handler<HttpServerRequest, HttpServerResponse> handler) {
        this.requestHandler = handler;
        return this;
    }

    public void listen(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new TinyHttpServerInitializer());

            Channel ch = b.bind(port).sync().channel();

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
