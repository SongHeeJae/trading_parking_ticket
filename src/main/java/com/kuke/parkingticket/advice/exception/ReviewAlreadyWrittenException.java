package com.kuke.parkingticket.advice.exception;

public class ReviewAlreadyWrittenException extends RuntimeException {
    public ReviewAlreadyWrittenException() {
    }

    public ReviewAlreadyWrittenException(String message) {
        super(message);
    }

    public ReviewAlreadyWrittenException(String message, Throwable cause) {
        super(message, cause);
    }
}
