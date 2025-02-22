package com.backend.user.exception;

public class ExistUserNameException extends RuntimeException {
    public ExistUserNameException(String message) {
        super(message);
    }
}
