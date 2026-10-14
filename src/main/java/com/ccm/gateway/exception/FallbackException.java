package com.ccm.gateway.exception;

public class FallbackException extends RuntimeException {
    public FallbackException(String message) {
        super(message);
    }

    public FallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}

