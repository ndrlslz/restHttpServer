package com.ndrlslz.model;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public class HttpServerRequest {
    private String uri;
    private HttpVersion protocolVersion;
    private HttpMethod method;
    private CaseInsensitiveMultiMap<CharSequence> headers;
    private Map<String, String> pathParams;
    private Map<String, String> queryParams;
    private String bodyAsString;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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

    public CaseInsensitiveMultiMap<CharSequence> getHeaders() {
        return headers;
    }

    public void setHeaders(CaseInsensitiveMultiMap<CharSequence> headers) {
        this.headers = headers;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getBodyAsString() {
        return bodyAsString;
    }

    public void setBodyAsString(String bodyAsString) {
        this.bodyAsString = bodyAsString;
    }
}
