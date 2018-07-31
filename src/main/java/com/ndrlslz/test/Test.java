package com.ndrlslz.test;

import com.ndrlslz.common.CaseInsensitiveMultiMap;
import com.ndrlslz.core.RestHttpServer;
import com.ndrlslz.json.Json;
import com.ndrlslz.model.HttpServerRequest;
import com.ndrlslz.router.RouterTable;
import com.ndrlslz.utils.ErrorBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Test {
    private Customers customers;

    public Test() {
        customers = new Customers();

        List<Customer> customerList = this.customers.getCustomers();
        customerList.add(new Customer("Tom", "Indian", 20));
        customerList.add(new Customer("Nick", "Beijing", 28));
    }

    public Customers getCustomers() {
        return customers;
    }

    public Optional<Customer> getCustomer(int id) {
        return customers
                .getCustomers()
                .stream()
                .filter(customer -> customer.getId() == id)
                .findFirst();
    }

    public Customer createCustomer(String name, String address, int age) {
        Customer customer = new Customer(name, address, age);
        customers.getCustomers().add(customer);
        return customer;
    }

    public static void main(String[] args) {
        Test test = new Test();

        RouterTable routerTable = new RouterTable();


        routerTable.get("/customers").handler(context -> {
            CaseInsensitiveMultiMap<String> queryParams = context.request().getQueryParams();
            String name = queryParams.get("name");

            Customers customers = test.getCustomers();
            if (name != null) {
                Customers result = new Customers();
                result.setCustomers(customers
                        .getCustomers()
                        .stream()
                        .filter(customer -> customer.getName().equals(name)).collect(Collectors.toList()));

                customers = result;
            }

            context.response().setBody(Json.encode(customers));
        });

        routerTable.get("/customers/{customer_id}").handler(context -> {
            int customerId = parseInt(context.request().getPathParams().get("customer_id"));

            Optional<Customer> customer = test.getCustomer(customerId);
            if (customer.isPresent()) {
                context.response().setBody(Json.encode(customer.get()));
            } else {
                context.response().setStatusCode(404);
                context.response().setBody(Json.encode(ErrorBuilder.newBuilder()
                        .withUri(context.request().getUri())
                        .withMessage("cannot find customer by id " + customerId)
                        .withStatus(404).build()
                ));
            }
        });

        routerTable.post("/customers").handler(context -> {
            String body = context.request().getBodyAsString();
            Customer customer = Json.decode(body, Customer.class);

            Customer createdCustomer = test.createCustomer(customer.getName(), customer.getAddress(), customer.getAge());
            context.response().setBody(Json.encode(createdCustomer));
        });

        routerTable.router("/test").handler(context -> {
            HttpServerRequest request = context.request();
            String NEW_LINE = "\r\n";
            StringBuilder builder = new StringBuilder();

            builder.append("Hello World").append(NEW_LINE);
            builder.append("Protocol Version: ").append(request.getProtocolVersion()).append(NEW_LINE);
            builder.append("Host: ").append(request.headers().get("host")).append(NEW_LINE);
            builder.append("URI: ").append(request.getUri()).append(NEW_LINE);
            builder.append("PATH: ").append(request.getPath()).append(NEW_LINE);
            builder.append("Method: ").append(request.getMethod()).append(NEW_LINE);
            builder.append("Content: ").append(request.getBodyAsString()).append(NEW_LINE);

            request.headers().each((key, value) -> builder.append("Header: ").append(key).append("=").append(value).append(NEW_LINE));

            request.getQueryParams().each((key, value) -> builder.append("Query: ").append(key).append("=").append(value).append(NEW_LINE));

            builder.append("Test: ").append("key").append("=").append(request.getQueryParams().get("key")).append(NEW_LINE);

            builder.append("DecoderResult: ").append(request.decoderResult()).append(NEW_LINE);

            context.response().setBody(builder.toString());
        });

        RestHttpServer
                .create()
                .requestHandler(routerTable)
                .listen(8080, result -> System.out.println(result.succeeded()));
    }
}
