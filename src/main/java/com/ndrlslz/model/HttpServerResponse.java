package com.ndrlslz.model;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import io.netty.handler.codec.http.HttpVersion;

public class HttpServerResponse {
    private int statusCode;
    private HttpVersion protocolVersion;
    private CaseInsensitiveMultiMap<String> headers = new CaseInsensitiveMultiMap<>();
    private String bodyAsString;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public CaseInsensitiveMultiMap<String> headers() {
        return headers;
    }

    public void setHeaders(CaseInsensitiveMultiMap<String> headers) {
        this.headers = headers;
    }

    public String getBodyAsString() {
        return bodyAsString;
    }

    public void setBodyAsString(String bodyAsString) {
        this.bodyAsString = bodyAsString;
    }
}
