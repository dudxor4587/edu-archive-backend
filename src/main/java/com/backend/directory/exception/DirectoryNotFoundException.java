package com.backend.directory.exception;

public class DirectoryNotFoundException extends RuntimeException {
    public DirectoryNotFoundException(String message) {
        super(message);
    }
}
