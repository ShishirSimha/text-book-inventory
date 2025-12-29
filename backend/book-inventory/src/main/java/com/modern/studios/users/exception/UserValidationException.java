package com.modern.studios.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User validation failed")
public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}