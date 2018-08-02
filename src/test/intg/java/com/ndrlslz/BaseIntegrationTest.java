package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.router.RouterTable;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.After;
import org.junit.Before;

public abstract class BaseIntegrationTest {
    private static final int PORT = 8888;
    private RestHttpServer httpServer;
    RouterTable routerTable;

    @Before
    public void setUp() {
        routerTable = new RouterTable();

        httpServer = RestHttpServer
                .create()
                .requestHandler(routerTable)
                .listen(PORT);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = PORT;
        RestAssured.registerParser("text/plain", Parser.HTML);
    }

    @After
    public void tearDown() {
        httpServer.close();
    }
}
