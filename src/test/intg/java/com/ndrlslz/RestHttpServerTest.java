package com.ndrlslz;

import com.ndrlslz.json.Json;
import com.ndrlslz.model.Customer;
import com.ndrlslz.service.CustomerService;
import com.ndrlslz.utils.ErrorBuilder;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

public class RestHttpServerTest extends BaseIntegrationTest {
    private CustomerService customerService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        Customer.idCreator.set(0);
        customerService = new CustomerService();
    }

    @Test
    public void shouldGetCustomers() {
        routerTable.get("/customers").handler(context -> {
            context.response().setBody(Json.encode(customerService.getCustomers()));
        });

        RestAssured.get("/customers")
                .then()
                .statusCode(200)
                .body("customers.size()", is(2))
                .body("customers.name", hasItems("Tom", "Nick"))
                .body("customers.age", hasItems(20, 28))
                .body("customers.id", hasItems(1, 2));
    }

    @Test
    public void shouldGetCustomer() {
        routerTable.get("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");

            Optional<Customer> customer = customerService.getCustomer(Integer.parseInt(customerId));
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
            }
        });

        RestAssured.get("/customers/1")
                .then()
                .statusCode(200)
                .body("name", is("Tom"))
                .body("age", is(20));
    }

    @Test
    public void shouldGetNotFoundGivenCustomerIdNotExists() {
        routerTable.get("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");

            Optional<Customer> customer = customerService.getCustomer(Integer.parseInt(customerId));
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
                context.response().setBody(Json.encode(ErrorBuilder.newBuilder()
                        .withUri(context.request().getUri())
                        .withMessage("cannot find customer by id " + customerId)
                        .withStatus(404).build()));
            }
        });

        RestAssured.get("/customers/3")
                .then()
                .statusCode(404)
                .body("message", is("cannot find customer by id 3"))
                .body("status", is(404));
    }

    @Test
    public void shouldGetCustomerGivenSearchCustomer() {
        routerTable.get("/customers").handler(context -> {
            String name = context.request().getQueryParams().get("name");

            Optional<Customer> customer = customerService.searchCustomerByName(name);
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
            }
        });

        RestAssured.get("/customers?name=Tom")
                .then()
                .statusCode(200)
                .body("name", is("Tom"))
                .body("age", is(20))
                .body("id", is(1));
    }

    @Test
    public void shouldCreateCustomer() {
        routerTable.post("/customers").handler(context -> {
            String body = context.request().getBodyAsString();
            Customer customer = Json.decode(body, Customer.class);

            Customer createdCustomer = customerService.createCustomer(customer.getName(), customer.getAddress(), customer.getAge());
            context.response().setBody(Json.encode(createdCustomer));
        });

        Customer createCustomer = new Customer("Sally", "American", 30);
        Customer.idCreator.decrementAndGet();

        given()
                .body(createCustomer)
                .post("/customers")
                .then()
                .statusCode(200)
                .body("name", is("Sally"))
                .body("age", is(30))
                .body("id", is(3));
    }
}