package com.kuke.parkingticket.advice.exception;

public class UserNicknameAlreadyException extends RuntimeException{
    public UserNicknameAlreadyException() {
    }

    public UserNicknameAlreadyException(String message) {
        super(message);
    }

    public UserNicknameAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }
}
