package com.decade.practice.users.domain;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public User createUser(UUID id, String username, String password, String name, String avatar, Date dob, Float gender) {
        password = passwordEncoder.encode(password);
        return new User(id, username, password, name, avatar, dob, gender);
    }
}
