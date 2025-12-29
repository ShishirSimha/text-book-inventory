package com.modern.studios.users.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import com.modern.studios.users.dto.forgotpassword.ForgotPasswordDTO;
import com.modern.studios.users.dto.login.LoginUserDTO;
import com.modern.studios.users.dto.register.RegisterUserDTO;
import com.modern.studios.users.entity.User;
import com.modern.studios.users.repository.UserRepository;

/**
 * Service class responsible for user authentication operations including
 * user registration, login authentication, and password reset functionality.
 */
@Service
public class AuthenticationService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with the provided email address";
    private static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email address is already registered. Please use a different email.";
    private static final String PASSWORD_RESET_SUCCESS_MESSAGE = "Password has been successfully reset";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor for AuthenticationService with dependency injection.
     *
     * @param userRepository        Repository for user data operations
     * @param authenticationManager Spring Security authentication manager
     * @param passwordEncoder       Password encoder for secure password handling
     * @param jwtService            JWT service for token operations
     */
    public AuthenticationService(UserRepository userRepository, 
                               AuthenticationManager authenticationManager, 
                               PasswordEncoder passwordEncoder,
                               JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user in the system.
     *
     * @param registerUserDTO User registration data
     * @return The newly created user entity
     * @throws IllegalArgumentException if email is already in use
     */
    @Transactional
    public User signup(RegisterUserDTO registerUserDTO) {
        validateEmailNotExists(registerUserDTO.email());
        User newUser = createUserFromRegistrationData(registerUserDTO);
        return userRepository.save(newUser);
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param loginUserDTO User login credentials
     * @return The authenticated user entity
     * @throws UsernameNotFoundException if user is not found
     */
    public User authenticate(LoginUserDTO loginUserDTO) {
        if (loginUserDTO == null) {
            throw new IllegalArgumentException("Login credentials cannot be null");
        }
        
        String email = loginUserDTO.email();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        performAuthentication(loginUserDTO);
        
        return userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    /**
     * Resets the password for a user.
     *
     * @param forgotPasswordDTO Password reset data containing email and new password
     * @return Success message
     * @throws UsernameNotFoundException if user is not found
     */
    @Transactional
    public String resetPassword(ForgotPasswordDTO forgotPasswordDTO) {
        User user = userRepository.findByEmail(forgotPasswordDTO.email())
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
        
        updateUserPassword(user, forgotPasswordDTO.password());
        userRepository.save(user);
        
        return PASSWORD_RESET_SUCCESS_MESSAGE;
    }

    /**
     * Logs out the user by invalidating their JWT token and destroying their current session.
     * 
     * @throws SessionAuthenticationException if the user is not authenticated
     */
    public void logout() {
        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SessionAuthenticationException("User is not authenticated");
        }
        
        // Extract JWT token from request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            
            // Invalidate the token by adding it to blacklist
            jwtService.invalidateToken(jwtToken);
        }
        
        // Clear the security context to destroy the session
        SecurityContextHolder.clearContext();
    }


    /**
     * Validates that the email is not already registered.
     *
     * @param email Email to validate
     * @throws IllegalArgumentException if email already exists
     */
    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }

    /**
     * Creates a new User entity from registration data.
     *
     * @param registerUserDTO Registration data
     * @return New User entity with encoded password
     */
    private User createUserFromRegistrationData(RegisterUserDTO registerUserDTO) {
        String encodedPassword = passwordEncoder.encode(registerUserDTO.password());
        
        return new User()
                .setFirstName(registerUserDTO.firstName())
                .setLastName(registerUserDTO.lastName())
                .setEmail(registerUserDTO.email())
                .setPassword(encodedPassword);
    }

    /**
     * Performs authentication using Spring Security.
     *
     * @param loginUserDTO Login credentials
     * @throws IllegalArgumentException if email or password is null or empty
     */
    private void performAuthentication(LoginUserDTO loginUserDTO) {
        if (loginUserDTO == null) {
            throw new IllegalArgumentException("Login credentials cannot be null");
        }
        
        String email = loginUserDTO.email();
        String password = loginUserDTO.password();
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                        email.trim(),
                        password
                );
        
        authenticationManager.authenticate(authToken);
    }

    /**
     * Updates the user's password with a new encoded password.
     *
     * @param user        User entity to update
     * @param newPassword New password to encode and set
     */
    private void updateUserPassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
    }
}
