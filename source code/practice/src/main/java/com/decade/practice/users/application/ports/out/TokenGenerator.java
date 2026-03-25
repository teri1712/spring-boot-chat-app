package com.decade.practice.users.application.ports.out;

import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.web.security.UserClaims;
import io.jsonwebtoken.JwtException;

public interface TokenGenerator {
      AccessToken generate(UserClaims userClaims);

      String generateRefreshToken(UserClaims userClaims);

      UserClaims decode(String token) throws JwtException;
}
