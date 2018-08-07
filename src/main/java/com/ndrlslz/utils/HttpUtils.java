package com.ndrlslz.utils;

import com.ndrlslz.model.HttpServerMessage;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;

public class HttpUtils {
    public static boolean isKeepAlive(HttpServerMessage message) {
        return message.headers().get(CONNECTION) != null &&
                message.headers().get(CONNECTION).contentEquals(HttpHeaderValues.KEEP_ALIVE);
    }

    public static boolean is100ContinueExpected(HttpServerRequest request) {
        if (request.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            return false;
        }

        String expect = request.headers().get(HttpHeaderNames.EXPECT);
        return HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(expect);
    }
}
