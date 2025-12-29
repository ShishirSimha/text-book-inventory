package com.modern.studios.users.service;

import java.util.List;
import java.util.stream.Collectors;

import com.modern.studios.users.exception.CannotModifyEmailException;
import com.modern.studios.users.exception.UserNotFoundException;
import com.modern.studios.users.exception.UserValidationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modern.studios.users.dto.admin.UserDetails;
import com.modern.studios.users.entity.User;
import com.modern.studios.users.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the database
     * @return List of UserDetails
     * @throws RuntimeException if database access fails
     */
    public List<UserDetails> getAllUsers() {
        try {
            logger.debug("Fetching all users from database");
            List<UserDetails> users = userRepository.findAll().stream()
                    .map(UserDetails::new)
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} users", users.size());
            return users;
        } catch (DataAccessException ex) {
            logger.error("Database error while fetching all users: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to retrieve users from database", ex);
        }
    }

    /**
     * Retrieves a user by email ID
     * @param emailId - The email ID to search for
     * @return UserDetails of the found user
     * @throws UserValidationException if email ID is blank
     * @throws UserNotFoundException if user is not found
     */
    public User getUserByEmailId(final String emailId) {
        try {
            logger.debug("Searching for user with email: {}", emailId);
            
            if(StringUtils.isBlank(emailId)) {
                logger.warn("Attempt to search user with blank email ID");
                throw new UserValidationException("Email ID is required");
            }
            
            User userDetails = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", emailId);
                        return new UserNotFoundException("User not found with email: " + emailId);
                    });
            
            logger.info("Successfully found user with email: {}", emailId);
            return userDetails;
            
        } catch (DataAccessException ex) {
            logger.error("Database error while searching for user with email {}: {}", emailId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to retrieve user from database", ex);
        }
    }

    /**
     * Method to update a user's details
     * @param emailId - The email ID of the user to update
     * @param userDetails - The user details to update
     * @return The updated user details
     * @throws UserValidationException if the email ID is blank or validation fails
     * @throws UserNotFoundException if user is not found
     * @throws CannotModifyEmailException if email modification is attempted
     * @throws IllegalArgumentException if created date modification is attempted
     */
    @Transactional
    public UserDetails updateUser(String emailId, UserDetails userDetails) {
        try {
            logger.debug("Attempting to update user with email: {}", emailId);
            
            // Validate input parameters
            if(StringUtils.isBlank(emailId)) {
                logger.warn("Attempt to update user with blank email ID");
                throw new UserValidationException("Email ID is required");
            }
            
            if(userDetails == null) {
                logger.warn("Attempt to update user with null user details");
                throw new UserValidationException("User details are required");
            }
            
            // Get the user of the given email id
            User user = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> {
                        logger.warn("User not found for update with email: {}", emailId);
                        return new UserNotFoundException("User not found with email: " + emailId);
                    });

            // Validate if the email id is being changed
            if(StringUtils.isNotBlank(userDetails.email()) && !userDetails.email().equals(user.getEmail())) {
                logger.warn("Attempt to modify email from {} to {}", user.getEmail(), userDetails.email());
                throw new CannotModifyEmailException("Email ID cannot be changed");
            }

            // Validate if the created date is being changed
            if(userDetails.createdAt() != null) {
                logger.warn("Attempt to modify created date for user: {}", emailId);
                throw new IllegalArgumentException("Created Date cannot be changed");
            }

            boolean isUpdated = false;
            
            // Update the user's first name if it is being changed
            if(StringUtils.isNotBlank(userDetails.firstName()) && !userDetails.firstName().equals(user.getFirstName())) {
                logger.debug("Updating first name for user {} from '{}' to '{}'", emailId, user.getFirstName(), userDetails.firstName());
                user.setFirstName(userDetails.firstName());
                isUpdated = true;
            }

            // Update the user's last name if it is being changed
            if(StringUtils.isNotBlank(userDetails.lastName()) && !userDetails.lastName().equals(user.getLastName())) {
                logger.debug("Updating last name for user {} from '{}' to '{}'", emailId, user.getLastName(), userDetails.lastName());
                user.setLastName(userDetails.lastName());
                isUpdated = true;
            }
            
            if (!isUpdated) {
                logger.info("No changes detected for user: {}", emailId);
                return new UserDetails(user);
            }
            
            // Save the user and return the updated user details
            User savedUser = userRepository.save(user);
            logger.info("Successfully updated user: {}", emailId);
            return new UserDetails(savedUser);
            
        } catch (DataAccessException ex) {
            logger.error("Database error while updating user {}: {}", emailId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to update user in database", ex);
        }
    }

}
