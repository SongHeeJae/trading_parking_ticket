package com.kuke.parkingticket.advice.exception;

public class InvalidateProviderException extends RuntimeException {
    public InvalidateProviderException() {
    }

    public InvalidateProviderException(String message) {
        super(message);
    }

    public InvalidateProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
