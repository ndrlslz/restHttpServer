package com.ndrlslz;

import com.ndrlslz.json.Json;
import com.ndrlslz.model.Customer;
import com.ndrlslz.service.CustomerService;
import com.ndrlslz.utils.ErrorBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

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

        get("/customers")
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

            Optional<Customer> customer = customerService.getCustomer(parseInt(customerId));
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
            }
        });

        get("/customers/1")
                .then()
                .statusCode(200)
                .body("name", is("Tom"))
                .body("age", is(20));
    }

    @Test
    public void shouldGetNotFoundGivenCustomerIdNotExists() {
        routerTable.get("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");

            Optional<Customer> customer = customerService.getCustomer(parseInt(customerId));
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

        get("/customers/3")
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

        get("/customers?name=Tom")
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

        assertThat(customerService.getCustomers().getCustomers().size(), is(3));
    }

    @Test
    public void shouldRemoveCustomer() {
        routerTable.delete("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");

            customerService.removeCustomer(parseInt(customerId));
        });

        given()
                .delete("/customers/1")
                .then()
                .statusCode(200);

        assertThat(customerService.getCustomers().getCustomers().size(), is(1));
    }

    @Test
    public void shouldUpdateCustomerGivenPutMethod() {
        routerTable.put("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");
            Customer updateCustomerBody = Json.decode(context.request().getBodyAsString(), Customer.class);

            Customer newCustomer = customerService.updateCustomerViaPut(parseInt(customerId), updateCustomerBody);
            context.response().setBody(Json.encode(newCustomer));
        });

        Customer updateNameAndDeleteAddressRequest = new Customer("Jim", null, 20);
        given()
                .body(updateNameAndDeleteAddressRequest)
                .put("/customers/1")
                .then()
                .statusCode(200);

        assertThat(customerService.getCustomers().getCustomers().get(0).getName(), is("Jim"));
        assertThat(customerService.getCustomers().getCustomers().get(0).getAddress(), is(nullValue()));
    }

    @Test
    public void shouldUpdateCustomerGivenPatchMethod() {
        routerTable.patch("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");
            Customer updateCustomerBody = Json.decode(context.request().getBodyAsString(), Customer.class);

            Customer newCustomer = customerService.updateCustomerViaPatch(parseInt(customerId), updateCustomerBody);
            context.response().setBody(Json.encode(newCustomer));
        });

        Customer updateAddressRequest = new Customer(null, "Chengdu", null);
        given()
                .body(updateAddressRequest)
                .patch("/customers/1")
                .then()
                .statusCode(200);

        assertThat(customerService.getCustomers().getCustomers().get(0).getName(), is("Tom"));
        assertThat(customerService.getCustomers().getCustomers().get(0).getAddress(), is("Chengdu"));
        assertThat(customerService.getCustomers().getCustomers().get(0).getAge(), is(20));
    }
}