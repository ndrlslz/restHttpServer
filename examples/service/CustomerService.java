package com.ndrlslz.service;


import com.ndrlslz.model.Customer;
import com.ndrlslz.model.Customers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomerService {
    private Customers customers;

    public CustomerService() {
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

    public void removeCustomer(int id) {
        customers.getCustomers().removeIf(customer -> customer.getId() == id);
    }

    public Optional<Customer> searchCustomerByName(String name) {
        return customers
                .getCustomers()
                .stream()
                .filter(customer -> customer.getName().equals(name))
                .findFirst();
    }

    public Customer updateCustomerViaPut(int id, Customer customer) {
        Optional<Customer> originCustomer = getCustomer(id);
        if (originCustomer.isPresent()) {
            Customer newCustomer = originCustomer.get();
            newCustomer.setName(customer.getName());
            newCustomer.setAddress(customer.getAddress());
            newCustomer.setAge(customer.getAge());
            return newCustomer;
        } else {
            return null;
        }
    }

    public Customer updateCustomerViaPatch(int id, Customer customer) {
        Optional<Customer> originCustomer = getCustomer(id);
        if (originCustomer.isPresent()) {
            Customer newCustomer = originCustomer.get();
            updateIfNotNull(customer.getName(), newCustomer::setName);
            updateIfNotNull(customer.getAddress(), newCustomer::setAddress);
            updateIfNotNull(customer.getAge(), newCustomer::setAge);

            return newCustomer;
        }
        return null;
    }

    private <T> void updateIfNotNull(T field, Function<T> function) {
        if (Objects.nonNull(field)) {
            function.apply(field);
        }
    }
}

interface Function<I> {
    void apply(I input);
}