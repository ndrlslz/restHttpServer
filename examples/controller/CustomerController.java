package com.ndrlslz.controller;

import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.json.Json;
import com.ndrlslz.model.Customer;
import com.ndrlslz.router.RouterTable;
import com.ndrlslz.service.CustomerService;
import com.ndrlslz.utils.ErrorBuilder;

import java.util.Optional;

import static java.lang.Integer.parseInt;

public class CustomerController {
    private CustomerService customerService = new CustomerService();

    public static void main(String[] args) {
        new CustomerController().startServer();
    }

    private void startServer() {
        RouterTable routerTable = new RouterTable();

        RestHttpServer
                .create()
                .requestHandler(routerTable)
                .listen(8080);

        getCustomersRouter(routerTable);

        getCustomerRouter(routerTable);

        searchCustomerRouter(routerTable);

        createCustomerRouter(routerTable);

        deleteCustomerRouter(routerTable);

        updateCustomerViaPutRouter(routerTable);

        updateCustomerViaPatchRouter(routerTable);
    }

    private void getCustomersRouter(RouterTable routerTable) {
        routerTable.get("/customers").handler(context -> {
            context.response().setBody(Json.encode(customerService.getCustomers()));
        });
    }

    private void getCustomerRouter(RouterTable routerTable) {
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
    }

    private void searchCustomerRouter(RouterTable routerTable) {
        routerTable.get("/customers").handler(context -> {
            String name = context.request().getQueryParams().get("name");

            Optional<Customer> customer = customerService.searchCustomerByName(name);
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
            }
        });
    }

    private void createCustomerRouter(RouterTable routerTable) {
        routerTable.post("/customers").handler(context -> {
            String body = context.request().getBodyAsString();
            Customer customer = Json.decode(body, Customer.class);

            Customer createdCustomer = customerService.createCustomer(customer.getName(), customer.getAddress(), customer.getAge());
            context.response().setBody(Json.encode(createdCustomer));
        });
    }

    private void deleteCustomerRouter(RouterTable routerTable) {
        routerTable.delete("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");

            customerService.removeCustomer(parseInt(customerId));
        });
    }

    private void updateCustomerViaPutRouter(RouterTable routerTable) {
        routerTable.put("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");
            Customer updateCustomerBody = Json.decode(context.request().getBodyAsString(), Customer.class);

            Customer newCustomer = customerService.updateCustomerViaPut(parseInt(customerId), updateCustomerBody);
            context.response().setBody(Json.encode(newCustomer));
        });
    }

    private void updateCustomerViaPatchRouter(RouterTable routerTable) {
        routerTable.patch("/customers/{customer_id}").handler(context -> {
            String customerId = context.request().getPathParams().get("customer_id");
            Customer updateCustomerBody = Json.decode(context.request().getBodyAsString(), Customer.class);

            Customer newCustomer = customerService.updateCustomerViaPatch(parseInt(customerId), updateCustomerBody);
            context.response().setBody(Json.encode(newCustomer));
        });
    }
}
