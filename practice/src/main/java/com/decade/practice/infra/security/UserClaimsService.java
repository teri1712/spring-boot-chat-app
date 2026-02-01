package com.decade.practice.infra.security;

import com.decade.practice.application.usecases.TokenStore;
import com.decade.practice.dto.TokenCredential;
import com.decade.practice.infra.security.models.UserClaims;
import org.springframework.security.access.AccessDeniedException;

public interface UserClaimsService extends TokenStore {

    void validate(String refreshToken) throws AccessDeniedException;

    UserClaims decodeToken(String token);

    TokenCredential create(UserClaims claims, String refreshToken);

}
