package com.modern.studios.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cannot modify Email of the user")
public class CannotModifyEmailException extends RuntimeException {
    public CannotModifyEmailException(String message) {
        super(message);
    }
}
