package com.decade.practice.adapter.security;

import com.decade.practice.adapter.security.models.UserClaims;
import com.decade.practice.application.usecases.TokenService;
import com.decade.practice.domain.TokenCredential;

public interface UserClaimsTokenService extends TokenService {

        UserClaims decodeToken(String token);

        TokenCredential create(UserClaims claims, String refreshToken);

}
