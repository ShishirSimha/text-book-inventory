package com.modern.studios.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.modern.studios.users.dto.forgotpassword.ForgotPasswordDTO;
import com.modern.studios.users.dto.login.LoginResponseDTO;
import com.modern.studios.users.dto.login.LoginUserDTO;
import com.modern.studios.users.dto.register.RegisterUserDTO;
import com.modern.studios.users.dto.register.RegisterUserResponseDTO;
import com.modern.studios.users.entity.User;
import com.modern.studios.users.service.AuthenticationService;
import com.modern.studios.users.service.JwtService;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(@Valid @RequestBody RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        RegisterUserResponseDTO registerUserResponseDTO = new RegisterUserResponseDTO("User registered successfully", "User registered successfully", registeredUser.getEmail());
        return ResponseEntity.ok(registerUserResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@Valid @RequestBody LoginUserDTO loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginUserDTO userDTO = new LoginUserDTO(authenticatedUser.getEmail(), null); // Don't send password in response
        LoginResponseDTO loginResponse = new LoginResponseDTO("Login successfully", jwtToken, userDTO);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return ResponseEntity.ok(authenticationService.resetPassword(forgotPasswordDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authenticationService.logout();
        return ResponseEntity.ok("Logout successfully");
    }

}
