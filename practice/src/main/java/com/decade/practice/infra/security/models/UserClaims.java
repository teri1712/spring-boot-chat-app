package com.decade.practice.infra.security.models;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class UserClaims {

    private UUID id;
    private String username;
    private String name;
    private String role;
    private Float gender;
    private ImageSpec avatar;

    public static UserClaims from(User user) {
        UserClaims userClaims = new UserClaims();
        userClaims.id = user.getId();
        userClaims.username = user.getUsername();
        userClaims.name = user.getName();
        userClaims.role = user.getRole();
        userClaims.gender = user.getGender();
        userClaims.avatar = user.getAvatar();
        return userClaims;
    }
}