package com.ndrlslz.codec;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.model.HttpServerRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class HttpServerRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    private HttpServerRequest httpServerRequest = new HttpServerRequest();

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) {
        assembleBasicInfo(request);

        assembleHeaders(request);

        assembleQueryParams(request);

        assembleBody(request);

        out.add(httpServerRequest);
    }

    private void assembleBody(FullHttpRequest request) {
        httpServerRequest.setBodyAsString(request.content().toString(Charset.defaultCharset()));
    }

    private void assembleQueryParams(HttpRequest request) {
        CaseInsensitiveMultiMap<String> queryMap = new CaseInsensitiveMultiMap<>();
        Map<String, List<String>> params = new QueryStringDecoder(request.uri()).parameters();

        if (!params.isEmpty()) {
            params.forEach((key, value1) -> value1.forEach(value -> queryMap.set(key, value)));
        }

        httpServerRequest.setQueryParams(queryMap);
    }

    private void assembleHeaders(HttpRequest request) {
        CaseInsensitiveMultiMap<String> headers = new CaseInsensitiveMultiMap<>();
        request.headers().entries().forEach(entry -> headers.set(entry.getKey(), entry.getValue()));
        httpServerRequest.setHeaders(headers);
    }

    private void assembleBasicInfo(HttpRequest request) {
        httpServerRequest.setMethod(request.method());
        httpServerRequest.setProtocolVersion(request.protocolVersion());
        httpServerRequest.setUri(request.uri());
        httpServerRequest.setDecoderResult(request.decoderResult());
    }
}
