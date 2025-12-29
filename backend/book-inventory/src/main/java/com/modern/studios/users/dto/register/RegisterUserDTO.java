package com.modern.studios.users.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserDTO(
        @NotEmpty(message = "The email address is required.")
        @Email(message = "The email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
        String email,

        @NotEmpty(message = "The password name is required.")
        @Size(min = 2, max = 25, message = "The length of password name must be between 2 and 25 characters.")
        String password,

        @NotEmpty(message = "The First name is required.")
        @Size(min = 2, max = 100, message = "The length of First name must be between 2 and 100 characters.")
        String firstName,

        @NotEmpty(message = "The First name is required.")
        @Size(min = 2, max = 100, message = "The length of First name must be between 2 and 100 characters.")
        String lastName
) {
}
