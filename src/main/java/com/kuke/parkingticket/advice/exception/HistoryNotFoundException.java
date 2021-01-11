package com.kuke.parkingticket.advice.exception;

public class HistoryNotFoundException extends RuntimeException {
    public HistoryNotFoundException() {
    }

    public HistoryNotFoundException(String message) {
        super(message);
    }

    public HistoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
