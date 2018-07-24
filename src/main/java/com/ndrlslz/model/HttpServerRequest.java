package com.ndrlslz.model;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public class HttpServerRequest implements HttpServerMessage {
    private String uri;
    private HttpVersion protocolVersion;
    private HttpMethod method;
    private CaseInsensitiveMultiMap<String> headers;
    private Map<String, String> pathParams;
    private CaseInsensitiveMultiMap<String> queryParams;
    private String bodyAsString;
    private DecoderResult decoderResult;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public CaseInsensitiveMultiMap<String> headers() {
        return headers;
    }

    @Override
    public DecoderResult decoderResult() {
        return decoderResult;
    }

    public void setDecoderResult(DecoderResult decoderResult) {
        this.decoderResult = decoderResult;
    }

    public void setHeaders(CaseInsensitiveMultiMap<String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public CaseInsensitiveMultiMap<String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(CaseInsensitiveMultiMap<String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getBodyAsString() {
        return bodyAsString;
    }

    public void setBodyAsString(String bodyAsString) {
        this.bodyAsString = bodyAsString;
    }
}
