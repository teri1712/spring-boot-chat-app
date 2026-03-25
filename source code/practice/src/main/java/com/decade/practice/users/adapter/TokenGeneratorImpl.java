package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.out.TokenGenerator;
import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenGeneratorImpl implements TokenGenerator {
      private static final long ONE_WEEK = 7L * 24 * 60 * 60 * 1000L;
      private static final long FIFTEEN_MINUTES = 15 * 60 * 1000L;
      private final TokenService tokenService;

      @Override
      public AccessToken generate(UserClaims userClaims) {
            String accessToken = tokenService.encodeToken(userClaims, FIFTEEN_MINUTES);
            String refreshToken = generateRefreshToken(userClaims);
            return new AccessToken(accessToken, refreshToken);
      }

      @Override
      public String generateRefreshToken(UserClaims userClaims) {
            return tokenService.encodeToken(userClaims, ONE_WEEK);
      }

      @Override
      public UserClaims decode(String token) throws JwtException {
            return tokenService.decodeToken(token);
      }
}
