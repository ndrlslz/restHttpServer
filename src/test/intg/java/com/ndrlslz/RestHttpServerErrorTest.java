package com.ndrlslz;

import org.junit.Test;

import static io.restassured.RestAssured.get;
import static org.hamcrest.CoreMatchers.is;

public class RestHttpServerErrorTest extends BaseIntegrationTest {

    @Test
    public void shouldReturnFindMultipleRoutersError() {
        routerTable.get("/customers").handler(null);
        routerTable.router("/customers").handler(null);

        get("/customers")
                .then()
                .statusCode(500)
                .body("error.message", is("Find multiple routers"))
                .body("error.status", is(500));
    }

    @Test
    public void shouldReturnCannotFindRouterErrorGivenNoAnyRouters() {
        get("/customers")
                .then()
                .statusCode(500)
                .body("error.message", is("Cannot find available router"))
                .body("error.status", is(500));
    }

    @Test
    public void shouldReturnCannotFindRouterErrorGivenOnlyGlobalRouter() {
        routerTable.router().handler(context -> {});

        get("/customers")
                .then()
                .statusCode(500)
                .body("error.message", is("Cannot find available router"))
                .body("error.status", is(500));
    }

    @Test
    public void shouldReturnCannotFindHandle() {
        routerTable.router("/customers");

        get("/customers")
                .then()
                .statusCode(500)
                .body("error.message", is("Cannot find available handle"))
                .body("error.status", is(500));
    }
}
