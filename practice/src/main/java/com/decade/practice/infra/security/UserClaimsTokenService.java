package com.decade.practice.infra.security;

import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.application.usecases.TokenService;
import com.decade.practice.api.dto.TokenCredential;

public interface UserClaimsTokenService extends TokenService {

        UserClaims decodeToken(String token);

        TokenCredential create(UserClaims claims, String refreshToken);

}
