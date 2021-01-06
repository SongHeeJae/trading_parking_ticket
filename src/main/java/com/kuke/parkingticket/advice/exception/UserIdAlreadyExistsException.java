package com.kuke.parkingticket.advice.exception;

public class UserIdAlreadyExistsException extends RuntimeException {
    public UserIdAlreadyExistsException() {
    }

    public UserIdAlreadyExistsException(String message) {
        super(message);
    }

    public UserIdAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
