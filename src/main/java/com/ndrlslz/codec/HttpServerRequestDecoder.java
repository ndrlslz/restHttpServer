package com.ndrlslz.codec;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServerRequestDecoder extends MessageToMessageDecoder<HttpMessage> {
    private HttpServerRequest httpServerRequest = new HttpServerRequest();

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpMessage message, List<Object> out) {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            assembleBasicInfo(request);

            assembleHeaders(request);

            assembleQueryParams(request);
        }

        if (message instanceof HttpContent) {
            HttpContent content = (HttpContent) message;

            httpServerRequest.setBodyAsString(content.content().toString(Charset.defaultCharset()));
        }

        out.add(httpServerRequest);
    }

    private void assembleQueryParams(HttpRequest request) {
        HashMap<String, String> queryMap = new HashMap<>();
        Map<String, List<String>> params = new QueryStringDecoder(request.uri()).parameters();

        if (!params.isEmpty()) {
            params.forEach((key, value1) -> value1.forEach(value -> queryMap.put(key, value)));
        }

        httpServerRequest.setQueryParams(queryMap);
    }

    private void assembleHeaders(HttpRequest request) {
        CaseInsensitiveMultiMap<CharSequence> headers = new CaseInsensitiveMultiMap<>();
        request.headers().entries().forEach(entry -> headers.put(entry.getKey(), entry.getValue()));
        httpServerRequest.setHeaders(headers);
    }

    private void assembleBasicInfo(HttpRequest request) {
        httpServerRequest.setMethod(request.method());
        httpServerRequest.setProtocolVersion(request.protocolVersion());
        httpServerRequest.setUri(request.uri());
    }
}
