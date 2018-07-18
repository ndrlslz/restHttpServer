package com.ndrlslz.handler;

@FunctionalInterface
public interface RequestHandler<I, O> {
    O handle(I request);
}
