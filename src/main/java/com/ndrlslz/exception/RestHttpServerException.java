package com.ndrlslz.exception;

public class RestHttpServerException extends RuntimeException {
    public RestHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
