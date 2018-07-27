package com.ndrlslz.model;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpVersion;

public class HttpServerResponse implements HttpServerMessage {
    private int statusCode;
    private HttpVersion protocolVersion;
    private CaseInsensitiveMultiMap<String> headers = new CaseInsensitiveMultiMap<>();
    private String body;
    private DecoderResult decoderResult;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
