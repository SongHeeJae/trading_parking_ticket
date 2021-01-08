package com.kuke.parkingticket.advice.exception;

public class FileConvertException extends RuntimeException {
    public FileConvertException() {
    }

    public FileConvertException(String message) {
        super(message);
    }

    public FileConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
