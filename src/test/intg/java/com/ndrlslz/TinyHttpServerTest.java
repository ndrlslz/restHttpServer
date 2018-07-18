package com.ndrlslz;

import com.ndrlslz.core.TinyHttpServer;
import org.junit.Test;

public class TinyHttpServerTest {
    @Test
    public void test() {
        TinyHttpServer
                .create()
                .requestHandler(request -> null)
                .listen(8080);
    }
}
