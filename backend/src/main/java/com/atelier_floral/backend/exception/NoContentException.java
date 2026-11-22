package com.atelier_floral.backend.exception;

public class NoContentException extends RuntimeException {
    public NoContentException(String message) {
        super(message);
    }

    public NoContentException(String message, Throwable cause) {
        super(message, cause);
    }
}