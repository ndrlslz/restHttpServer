package com.ndrlslz.utils;

import com.ndrlslz.model.Error;

public class ErrorBuilder {
    private Error error;

    private ErrorBuilder() {
        error = new Error();
    }

    public static ErrorBuilder newBuilder() {
        return new ErrorBuilder();
    }

    public ErrorBuilder withMessage(String message) {
        error.setMessage(message);
        return this;
    }

    public ErrorBuilder withStatus(int status) {
        error.setStatus(status);
        return this;
    }

    public ErrorBuilder withException(String exception) {
        error.setException(exception);
        return this;
    }

    public ErrorBuilder withUri(String uri) {
        error.setUri(uri);
        return this;
    }

    public Error build() {
        return error;
    }
}
