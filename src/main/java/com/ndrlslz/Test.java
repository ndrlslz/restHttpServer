package com.ndrlslz;

import com.ndrlslz.core.TinyHttpServer;

public class Test {
    public static void main(String[] args) {
        TinyHttpServer
                .create()
                .requestHandler(request -> null)
                .listen(8080);

    }
}
