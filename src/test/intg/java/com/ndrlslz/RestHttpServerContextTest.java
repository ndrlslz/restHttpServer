package com.ndrlslz;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.model.HttpServerRequest;
import org.junit.Test;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RestHttpServerContextTest extends BaseIntegrationTest {
    @Test
    public void shouldRetrieveBasicRequestInformation() {
        routerTable.router("/customers").handler(context -> {
            HttpServerRequest request = context.request();

            assertThat(request.getUri(), is("/customers?name=Tom"));
            assertThat(request.getPath(), is("/customers"));
            assertThat(request.getMethod(), is(GET));

        });

        get("/customers?name=Tom")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldRetrievePathParameters() {
        routerTable.get("/customers/{customer_id}/orders/{order_id}").handler(context -> {
            Map<String, String> pathParams = context.request().getPathParams();

            assertThat(pathParams.get("customer_id"), is("123456"));
            assertThat(pathParams.get("order_id"), is("654321"));
        });

        get("/customers/123456/orders/654321")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldRetrieveQueryParameters() {
        routerTable.get("/customers").handler(context -> {
            CaseInsensitiveMultiMap<String> queryParams = context.request().getQueryParams();

            assertThat(queryParams.get("name"), is("Tom"));
            assertThat(queryParams.get("age"), is("20"));
        });

        get("/customers?name=Tom&age=20")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldRetrieveHeadersWithCaseInsensitive() {
        routerTable.get("/customers").handler(context -> {
            CaseInsensitiveMultiMap<String> headers = context.request().headers();

            assertThat(headers.get("Key"), is("value"));
            assertThat(headers.get("key"), is("value"));
            assertThat(headers.get("KEY"), is("value"));
        });

        given()
                .header("Key", "value")
                .get("/customers")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldRetrieveBody() {
        routerTable.post("/customers").handler(context -> {
            String body = context.request().getBodyAsString();

            assertThat(body, is("hello world!"));
        });

        given()
                .body("hello world!")
                .post("/customers")
                .then()
                .statusCode(200);
    }


    @Test
    public void shouldSetResponseStatusCode() {
        routerTable.router("/customers").handler(context -> context.response().setStatusCode(300));

        get("/customers")
                .then()
                .statusCode(300);
    }

    @Test
    public void shouldSetResponseHeadersWithCaseInsensitive() {
        routerTable.get("/customers").handler(context -> {
            CaseInsensitiveMultiMap<String> headers = context.response().headers();

            headers.set("Key", "value");
        });

        get("/customers")
                .then()
                .statusCode(200)
                .header("Key", "value")
                .header("key", "value")
                .header("KEY", "value");
    }

    @Test
    public void shouldSetResponseBody() {
        routerTable.get("/customers").handler(context -> {
            context.response().headers().set(CONTENT_TYPE, TEXT_PLAIN.toString());
            context.response().setBody("hello world!");
        });

        get("/customers")
                .then()
                .statusCode(200)
                .body("html.body", containsString("hello world"));
    }
}