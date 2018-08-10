package com.ndrlslz.core;

public class RestHttpServerOptions {
    private Integer workerThreadCount;

    public RestHttpServerOptions withWorkerThreadCount(int count) {
        this.workerThreadCount = count;
        return this;
    }

    public Integer getWorkerThreadCount() {
        return workerThreadCount;
    }
}
