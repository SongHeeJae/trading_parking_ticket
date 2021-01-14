package com.kuke.parkingticket.advice.exception;

public class RegionNotFoundException extends RuntimeException {
    public RegionNotFoundException() {
    }

    public RegionNotFoundException(String message) {
        super(message);
    }

    public RegionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
