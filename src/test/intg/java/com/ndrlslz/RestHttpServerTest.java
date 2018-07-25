package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.router.RouterTable;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;

public class RestHttpServerTest {
    private static final int PORT = 8888;
    private RestHttpServer httpServer;

    @Before
    public void setUp() throws Exception {
        RouterTable routerTable = new RouterTable();

        routerTable.router("/").handler(context -> System.out.println(context.request().getUri()));

        httpServer = Executors.newCachedThreadPool().submit(() -> RestHttpServer
                .create()
                .requestHandler(routerTable)
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
    public void get() {
        given()
                .when()
                .get("/test")
                .then()
                .statusCode(200)
                .body("html.body", Matchers.containsString("Hello World"));
    }

    @Test
    public void post() {
        given()
                .body("{\"test\": 123}")
                .when()
                .post("/test")
                .then()
                .statusCode(200)
                .body("html.body", Matchers.containsString("Hello World"))
                .body("html.body", Matchers.containsString("Content: {\"test\": 123}"));
    }
}
