package com.example.webapp_backend.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}