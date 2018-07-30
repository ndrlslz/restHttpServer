package com.ndrlslz.utils;

import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.model.HttpServerResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

import java.util.Collection;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HttpServerResponseBuilder {
    private HttpServerResponse httpServerResponse;

    private HttpServerResponseBuilder() {
        httpServerResponse = new HttpServerResponse();
        withHeader(CONTENT_TYPE, APPLICATION_JSON);
    }

    public static HttpServerResponseBuilder newBuilder() {
        return new HttpServerResponseBuilder();
    }

    public HttpServerResponseBuilder withStatusCode(int statusCode) {
        httpServerResponse.setStatusCode(statusCode);
        return this;
    }

    public static HttpServerResponseBuilder ok() {
        return newBuilder().withStatusCode(OK.code());
    }

    public static HttpServerResponseBuilder internalServerError() {
        return newBuilder().withStatusCode(INTERNAL_SERVER_ERROR.code());
    }

    public HttpServerResponseBuilder withProtocolVersion(HttpVersion httpVersion) {
        httpServerResponse.setProtocolVersion(httpVersion);
        return this;
    }

    public HttpServerResponseBuilder withHeader(AsciiString key, AsciiString value) {
        httpServerResponse.headers().set(key, value.toString());
        return this;
    }

    public HttpServerResponseBuilder withHeader(AsciiString key, Collection<AsciiString> value) {
        httpServerResponse.headers().set(key, value.toString());
        return this;
    }

    public HttpServerResponseBuilder withBody(String body) {
        httpServerResponse.setBody(body);
        return this;
    }

    public HttpServerResponseBuilder withRequest(HttpServerRequest request) {
        httpServerResponse.setProtocolVersion(request.getProtocolVersion());
        return this;
    }

    public HttpServerResponse build() {
        return httpServerResponse;
    }
}
