package com.cassiomolin.example.security.exception;

public class MultipleUserException extends RuntimeException {
	
    public MultipleUserException(String message) {
        super(message);
    }

    public MultipleUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
