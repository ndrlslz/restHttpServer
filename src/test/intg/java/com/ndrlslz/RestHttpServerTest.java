package com.ndrlslz;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.handler.Handler;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.router.RouterContext;
import com.ndrlslz.router.RouterTable;
import io.netty.handler.codec.http.HttpMethod;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestHttpServerTest {
    private static final int PORT = 8888;
    private RestHttpServer httpServer;
    private RouterTable routerTable;

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

    private Handler<RouterContext> requestHandler() {
        return context -> {
            HttpServerRequest request = context.request();
            String NEW_LINE = "\r\n";
            StringBuilder builder = new StringBuilder();

            builder.append("Hello World").append(NEW_LINE);
            builder.append("Protocol Version1: ").append(request.getProtocolVersion()).append(NEW_LINE);
            builder.append("Host: ").append(request.headers().get("host")).append(NEW_LINE);
            builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
            builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
            builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);

            request.headers().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));

            request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));

            builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key")).append(NEW_LINE);

            builder.append("DecoderResult: ").append(request.decoderResult()).append(NEW_LINE);

            context.response().setBody(builder.toString());
        };
    }

    @After
    public void tearDown() {
        httpServer.close();
    }

    @Test
    public void get() {
        routerTable.get("/testGet").handler(requestHandler());

        given()
                .when()
                .get("/testGet")
                .then()
                .statusCode(200)
                .body("html.body", Matchers.containsString("Hello World"));
    }

    @Test
    public void post() {
        routerTable.post("/testPost").handler(requestHandler());


        given()
                .body("{\"test\": 123}")
                .when()
                .post("/testPost")
                .then()
                .statusCode(200)
                .body("html.body", Matchers.containsString("Hello World"))
                .body("html.body", Matchers.containsString("Content: {\"test\": 123}"));
    }

    @Test
    public void testGetOrders() {
        routerTable.router("/orders", HttpMethod.GET).handler(context -> {
            context.response().setBody("{\"name\": \"car\", \"price\" : 123}");
            context.response().headers().set(CONTENT_TYPE, APPLICATION_JSON.toString());
        });

        given()
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("name", is("car"))
                .body("price", is(123));
    }

    @Test
    public void testGetOrdersWithPathParams() {
        routerTable.router("/customers/{customerID}/orders/{orderID}").handler(context -> {
            assertThat(context.request().getPathParams().get("customerID"), is("100"));
            assertThat(context.request().getPathParams().get("orderID"), is("ABC"));

            context.response().setBody("{\"name\": \"car\", \"price\" : 123}");
            context.response().headers().set(CONTENT_TYPE, APPLICATION_JSON.toString());
        });

        given()
                .when()
                .get("/customers/100/orders/ABC")
                .then()
                .statusCode(200)
                .body("name", is("car"))
                .body("price", is(123));

    }
}