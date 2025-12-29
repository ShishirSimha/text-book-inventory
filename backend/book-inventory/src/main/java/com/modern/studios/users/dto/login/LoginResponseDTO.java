package com.modern.studios.users.dto.login;

public record LoginResponseDTO(String message,
                               String token,
                               LoginUserDTO data) {
}
