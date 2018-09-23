package com.ndrlslz.model;

import java.util.ArrayList;
import java.util.List;

public class Customers {
    private List<Customer> customers = new ArrayList<>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
