package com.ndrlslz.model;

import com.ndrlslz.common.MultiMap;

public class HttpServerRequest {
    private String uri;
    private String protocolVersion;
    private HttpMethod method;
    private MultiMap<String, String> headers;
}
