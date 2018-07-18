package com.ndrlslz.handler;

@FunctionalInterface
public interface Handler<I, O> {
    O handle(I request);
}
