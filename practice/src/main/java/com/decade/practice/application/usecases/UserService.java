package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.AccountResponse;
import com.decade.practice.api.dto.ProfileRequest;
import com.decade.practice.api.dto.SignUpRequest;
import com.decade.practice.persistence.jpa.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public interface UserService {

    User create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;
    
    User changeProfile(UUID id, ProfileRequest profileRequest);

    User changePassword(UUID id, String newPassword, String password) throws AccessDeniedException;

    AccountResponse prepareAccount(String username);

}
