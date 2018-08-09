package com.ndrlslz.utils;

import com.ndrlslz.model.HttpServerMessage;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class HttpUtils {
    public static boolean isKeepAlive(HttpServerMessage message) {
        if (message.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) >= 0) {
            return isNull(message.headers().get(CONNECTION)) ||
                    !message.headers().get(CONNECTION).equalsIgnoreCase(HttpHeaderValues.CLOSE.toString());
        } else {
            return nonNull(message.headers().get(CONNECTION)) &&
                    message.headers().get(CONNECTION).equalsIgnoreCase(HttpHeaderValues.KEEP_ALIVE.toString());
        }
    }

    public static boolean is100ContinueExpected(HttpServerRequest request) {
        if (request.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            return false;
        }

        String expect = request.headers().get(HttpHeaderNames.EXPECT);
        return HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(expect);
    }
}
