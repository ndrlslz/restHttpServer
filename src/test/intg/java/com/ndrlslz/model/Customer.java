package com.ndrlslz.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Customer {
    public static AtomicInteger idCreator = new AtomicInteger();
    private int id;
    private String name;
    private String address;
    private int age;

    public Customer(String name, String address, int age) {
        this.id = idCreator.incrementAndGet();
        this.name = name;
        this.address = address;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
