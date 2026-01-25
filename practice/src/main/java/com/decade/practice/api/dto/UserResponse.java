package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
public class UserResponse {

    private String username;
    private String name;
    private Date dob;
    private String role;
    private UUID id;
    private ImageSpec avatar;
    private Float gender;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .dob(user.getDob())
                .role(user.getRole())
                .id(user.getId())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .build();
    }
}
