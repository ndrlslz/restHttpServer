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
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
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

            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
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
        routerTable.router("/customers/{customer_id}/orders/{order_id}").handler(context -> {
            assertThat(context.request().getPathParams().get("customer_id"), is("100"));
            assertThat(context.request().getPathParams().get("order_id"), is("ABC"));

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

    @Test
    public void testGetCustomersWithQueryParams() {
        routerTable.router("/customers").handler(context -> {
            String name = context.request().getQueryParams().get("name");
            String age = context.request().getQueryParams().get("age");

            assertThat(name, is("Tom"));
            assertThat(age, is("20"));

            context.response().setBody("{\"name\": \"" + name + "\", \"age\": " + age + "}");
        });

        given()
                .log()
                .all()
                .expect()
                .log()
                .all()
                .when()
                .get("/customers?name=Tom&age=20")
                .then()
                .statusCode(200)
                .body("name", is("Tom"))
                .body("age", is(20));
    }

    @Test
    public void shouldReturnCannotFindRouterError() {
        given()
                .when()
                .get("/test")
                .then()
                .statusCode(500)
                .body("error.message", is("Cannot find available router"))
                .body("error.status", is(500));

    }

    @Test
    public void shouldReturnFindMultipleRoutersError() {
        routerTable.get("/customers").handler(null);
        routerTable.router("/customers").handler(null);

        given()
                .when()
                .get("/customers")
                .then()
                .statusCode(500)
                .body("error.message", is("Find multiple routers"))
                .body("error.status", is(500));
    }

    @Test
    public void testEmptyResponseBody() {
        routerTable.router("/empty").handler(context -> context.response().headers().set("key", "value"));

        given()
                .when()
                .get("/empty?name=Tom&age=20")
                .then()
                .statusCode(200)
                .header("key", is("value"));
    }
}