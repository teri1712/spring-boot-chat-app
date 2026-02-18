package com.decade.practice.users.application.ports.in;

import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.SignUpRequest;
import com.decade.practice.users.dto.UserResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public interface UserService {

    UserResponse createIfNotExists(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;

    UserResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;

    UserResponse changeProfile(UUID id, ProfileRequest profileRequest);

    UserResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException;

    UserResponse findById(UUID id);

    UserResponse findByUsername(String username);

}
