package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;

public class Test {
    public static void main(String[] args) {
        RestHttpServer
                .create()
                .requestHandler(request -> null)
                .listen(8080);
    }
}
