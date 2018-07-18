package com.ndrlslz.handler;

@FunctionalInterface
public interface Handler<T> {

    void handle(T event);
}
