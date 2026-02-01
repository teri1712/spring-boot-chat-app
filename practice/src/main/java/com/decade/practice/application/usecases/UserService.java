package com.decade.practice.application.usecases;

import com.decade.practice.dto.AccountResponse;
import com.decade.practice.dto.ProfileRequest;
import com.decade.practice.dto.SignUpRequest;
import com.decade.practice.dto.UserResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public interface UserService {

    UserResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;

    UserResponse changeProfile(UUID id, ProfileRequest profileRequest);

    UserResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException;

    AccountResponse prepareAccount(String username);

}
