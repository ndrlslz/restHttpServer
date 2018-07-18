package com.ndrlslz;

import com.ndrlslz.core.TinyHttpServer;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;

public class TinyHttpServerTest {
    private static final int PORT = 8080;
    private TinyHttpServer httpServer;

    @Before
    public void setUp() throws Exception {
        httpServer = Executors.newCachedThreadPool().submit(() -> TinyHttpServer
                .create()
                .requestHandler(request -> null)
                .listen(PORT)).get();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = PORT;
        RestAssured.registerParser("text/plain", Parser.HTML);

    }

    @After
    public void tearDown() {
        httpServer.close();
    }

    @Test
    public void test() {
        given()
                .when()
                .get("/test")
                .then()
                .statusCode(200)
                .body("html.body", Matchers.containsString("Hello World"));
    }
}
