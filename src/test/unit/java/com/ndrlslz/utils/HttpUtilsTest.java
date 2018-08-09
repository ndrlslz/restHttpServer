package com.ndrlslz.utils;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpUtilsTest {

    @Test
    public void couldCreateHttpUtils() {
        new HttpUtils();
    }

    @Test
    public void shouldNotBeKeepAliveGivenNoKeepAliveHeaderAndOnePointZeroHttpVersion() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_0);

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(false));
    }

    @Test
    public void shouldNotBeKeepAliveGivenCloseHeaderAndOnePointZeroHttpVersion() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_0);
        request.headers().set(CONNECTION, HttpHeaderValues.CLOSE.toString());

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(false));
    }

    @Test
    public void shouldNotBeKeepAliveGivenCloseHeader() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_1);
        request.headers().set(CONNECTION, HttpHeaderValues.CLOSE.toString());

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(false));
    }

    @Test
    public void shouldBeKeepAliveGivenNoKeepAliveHeaderAndOnePointOneHttpVersion() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(true));
    }

    @Test
    public void shouldBeKeepAliveGivenKeepAliveHeaderAndOnePointZeroHttpVersion() {
        HttpServerRequest request = createRequest();
        request.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        request.setProtocolVersion(HttpVersion.HTTP_1_0);

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(true));
    }

    @Test
    public void shouldBeKeepAliveGivenKeepAliveHeaderAndOnePointOneHttpVersion() {
        HttpServerRequest request = createRequest();
        request.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        boolean keepAlive = HttpUtils.isKeepAlive(request);

        assertThat(keepAlive, is(true));
    }

    @Test
    public void shouldNotBe100ContinueGivenHttpVersionNotMatch() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_0);

        boolean continueExpected = HttpUtils.is100ContinueExpected(request);

        assertThat(continueExpected, is(false));
    }

    @Test
    public void shouldNotBe100ContinueGivenNoContinueHeader() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        boolean continueExpected = HttpUtils.is100ContinueExpected(request);

        assertThat(continueExpected, is(false));
    }

    @Test
    public void shouldBe100ContinueGivenCorrentHttpVersionAndContinueHeader() {
        HttpServerRequest request = createRequest();
        request.setProtocolVersion(HttpVersion.HTTP_1_1);
        request.headers().set(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE.toString());

        boolean continueExpected = HttpUtils.is100ContinueExpected(request);

        assertThat(continueExpected, is(true));

    }

    private HttpServerRequest createRequest() {
        HttpServerRequest request = new HttpServerRequest();
        request.setHeaders(new CaseInsensitiveMultiMap<>());
        return request;
    }
}