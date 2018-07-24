package com.ndrlslz.utils;

import com.ndrlslz.model.HttpServerMessage;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.handler.codec.http.HttpHeaderValues;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;

public class HttpUtils {
    public static boolean isKeepAlive(HttpServerMessage message) {
        return message.headers().get(CONNECTION) != null &&
                message.headers().get(CONNECTION).contentEquals(HttpHeaderValues.KEEP_ALIVE);
    }

    public static boolean is100ContinueExpected(HttpServerRequest request) {
        return false;
    }
}
