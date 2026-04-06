package com.decade.practice.users.application.ports.in;

import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.dto.SignUpRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public interface ProfileService {

      ProfileResponse createIfNotExists(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;

      ProfileResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException;

      ProfileResponse changeProfile(UUID id, ProfileRequest profileRequest);

      ProfileResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException;

      ProfileResponse findById(UUID id);

      ProfileResponse findByUsername(String username);

}
