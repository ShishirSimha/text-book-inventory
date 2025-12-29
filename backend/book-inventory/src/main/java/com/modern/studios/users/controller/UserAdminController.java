package com.modern.studios.users.controller;

import java.util.List;

import com.modern.studios.users.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.modern.studios.users.dto.admin.UserDetails;
import com.modern.studios.users.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private static final Logger logger = LoggerFactory.getLogger(UserAdminController.class);
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users
     * @return ResponseEntity containing list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserDetails>> getAllUsers() {
        logger.debug("Received request to get all users");
        List<UserDetails> users = userService.getAllUsers();
        logger.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by email ID
     * @param emailId - The email ID to search for
     * @return ResponseEntity containing user details
     */
    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmailId(@RequestParam("email") String emailId) {
        logger.debug("Received request to get user by email: {}", emailId);
        User userDetails = userService.getUserByEmailId(emailId);
        logger.info("Successfully retrieved user with email: {}", emailId);
        return ResponseEntity.ok(userDetails);
    }

    /**
     * Updates a user's details
     * @param emailId - The email ID of the user to update
     * @param userDetails - The updated user details
     * @return ResponseEntity containing updated user details
     */
    @PutMapping("/update")
    public ResponseEntity<UserDetails> updateUser(@RequestParam("email") String emailId, @RequestBody UserDetails userDetails) {
        logger.debug("Received request to update user with email: {}", emailId);
        UserDetails updatedUser = userService.updateUser(emailId, userDetails);
        logger.info("Successfully updated user with email: {}", emailId);
        return ResponseEntity.ok(updatedUser);
    }
}
