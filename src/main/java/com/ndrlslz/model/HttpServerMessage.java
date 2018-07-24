package com.ndrlslz.model;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpServerMessage {
    HttpVersion getProtocolVersion();

    CaseInsensitiveMultiMap<String> headers();

    DecoderResult decoderResult();
}
