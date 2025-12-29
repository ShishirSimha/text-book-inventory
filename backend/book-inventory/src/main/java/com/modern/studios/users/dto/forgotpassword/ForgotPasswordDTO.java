package com.modern.studios.users.dto.forgotpassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ForgotPasswordDTO(
        @NotEmpty(message = "The email address is required.")
        @Email(message = "The email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
        String email,

        @NotEmpty(message = "The password name is required.")
        @Size(min = 2, max = 25, message = "The length of password name must be between 2 and 25 characters.")
        String password
) {
}
