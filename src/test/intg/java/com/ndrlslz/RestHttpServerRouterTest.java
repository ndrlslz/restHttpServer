package com.ndrlslz;

import io.restassured.RestAssured;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;

public class RestHttpServerRouterTest extends BaseIntegrationTest {
    @Test
    public void shouldApplyGlobalConfigurationForAllHttpMethod() {
        routerTable.router().handler(context -> {
            context.response().setBody("This is global configuration");
            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
        });

        routerTable.router("/path").handler(context -> {
        });

        get("/path")
                .then()
                .statusCode(200)
                .body("html.body", is("This is global configuration"));
    }

    @Test
    public void shouldOverrideGlobalConfiguration() {
        routerTable.get().handler(context -> {
            context.response().setBody("This is global configuration");
            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
        });

        routerTable.get("/path").handler(context -> {
            context.response().setBody("This is override configuration");
            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
        });

        get("/path")
                .then()
                .statusCode(200)
                .body("html.body", is("This is override configuration"));
    }

    @Test
    public void shouldNotApplyPostGlobalConfigurationGivenGetRouter() {
        routerTable.get().handler(context -> {
            context.response().setBody("This is global configuration");
            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
        });

        routerTable.post("/path").handler(context -> context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString()));

        post("/path").then().statusCode(200).body("html.body", is(""));
    }

    @Test
    public void shouldNotMatchGivenDifferentPath() {
        routerTable.router("/").handler(context -> context.response().setStatusCode(200));

        get("/path-not-defined").then().statusCode(500);
    }

    @Test
    public void shouldMatchAllHttpMethodGivenHttpMethodNotDefined() {
        routerTable.router("/path").handler(context -> {
        });

        get("/path").then().statusCode(200);
        post("/path").then().statusCode(200);
        put("/path").then().statusCode(200);
        patch("/path").then().statusCode(200);
        delete("/path").then().statusCode(200);
    }

    @Test
    public void shouldNotMatchGivenPostMethodAndGetRequest() {
        routerTable.post("/path").handler(context -> {
        });

        RestAssured.get("path").then().statusCode(500);
    }
}
