package com.ndrlslz.exception;

public class TinyHttpServerException extends RuntimeException {
    public TinyHttpServerException(String message) {
        super(message);
    }

    public TinyHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
