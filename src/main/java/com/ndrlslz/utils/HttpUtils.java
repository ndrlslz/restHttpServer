package com.ndrlslz.utils;

import com.ndrlslz.model.HttpServerRequest;
import io.netty.handler.codec.http.HttpHeaderValues;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;

public class HttpUtils {
    public static boolean isKeepAlive(HttpServerRequest request) {
        return request.getHeaders().get(CONNECTION) != null &&
                request.getHeaders().get(CONNECTION).equals(HttpHeaderValues.KEEP_ALIVE);
    }

    public static boolean is100ContinueExpected(HttpServerRequest request) {
        return false;
    }
}
