package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
