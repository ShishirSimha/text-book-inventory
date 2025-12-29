package com.modern.studios.users.dto.admin;

import java.util.Date;

import com.modern.studios.users.entity.User;

public record UserDetails(String email, String firstName, String lastName, Date createdAt) {
    
    public UserDetails(User user) {
        this(user.getEmail(), user.getFirstName(), user.getLastName(), user.getCreatedAt());
    }
}
