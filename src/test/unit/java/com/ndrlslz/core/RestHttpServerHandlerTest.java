package com.ndrlslz.core;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.exception.RestHttpServerException;
import com.ndrlslz.handler.RequestHandler;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RestHttpServerHandlerTest {
    @InjectMocks
    private RestHttpServerHandler restHttpServerHandler;

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private ChannelHandlerContext channelHandlerContext;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSend100ContinueGivenContinueHeader() {
        HttpServerRequest request = createRequest();
        request.headers().set(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE.toString());

        restHttpServerHandler.channelRead0(channelHandlerContext, request);

        verify(channelHandlerContext).write(any());
        verify(channelHandlerContext).writeAndFlush(any());
        verifyNoMoreInteractions(channelHandlerContext);
    }

    @Test
    public void shouldNotSend100ContinueGivenNoContinueHeader() {
        HttpServerRequest request = createRequest();

        restHttpServerHandler.channelRead0(channelHandlerContext, request);

        verify(channelHandlerContext).writeAndFlush(any());
        verifyNoMoreInteractions(channelHandlerContext);
    }

    @Test(expected = RestHttpServerException.class)
    public void shouldThrowRequestHandlerCannotBeNull() {
        restHttpServerHandler = new RestHttpServerHandler(null);
        HttpServerRequest request = createRequest();
        request.headers().set(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE.toString());

        restHttpServerHandler.channelRead0(channelHandlerContext, request);
    }

    @Test
    public void shouldCatchException() {
        ChannelFuture channelFuture = Mockito.mock(ChannelFuture.class);
        when(channelHandlerContext.writeAndFlush(any())).thenReturn(channelFuture);

        restHttpServerHandler.exceptionCaught(channelHandlerContext, new RuntimeException(""));

        verify(channelHandlerContext).writeAndFlush(any());
    }

    private HttpServerRequest createRequest() {
        HttpServerRequest request = new HttpServerRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_1);
        request.setHeaders(new CaseInsensitiveMultiMap<>());
        return request;
    }
}