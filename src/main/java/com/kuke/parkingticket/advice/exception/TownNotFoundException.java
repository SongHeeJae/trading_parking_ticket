package com.kuke.parkingticket.advice.exception;

public class TownNotFoundException extends RuntimeException {
    public TownNotFoundException() {
    }

    public TownNotFoundException(String message) {
        super(message);
    }

    public TownNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
