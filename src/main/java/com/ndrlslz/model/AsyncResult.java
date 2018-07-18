package com.ndrlslz.model;

public class AsyncResult<T> {
    private T result;
    private Exception exception;
    private boolean success;

    private AsyncResult(T result) {
        this.result = result;
        this.success = true;
    }

    private AsyncResult(Exception exception) {
        this.exception = exception;
        this.success = false;
    }

    public static <T> AsyncResult<T> success(T result) {
        return new AsyncResult<>(result);
    }

    public static <T> AsyncResult<T> fail(Exception e) {
        return new AsyncResult<>(e);
    }

    public boolean succeeded() {
        return success;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
