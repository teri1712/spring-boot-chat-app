package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;

import java.util.Date;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String name,
        Date dob,
        String role,
        ImageSpec avatar,
        Float gender
) {
    public static UserResponse from(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getDob(),
                user.getRole(),
                user.getAvatar(),
                user.getGender()
        );
    }
}
