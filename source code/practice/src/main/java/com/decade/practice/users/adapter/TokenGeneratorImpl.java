package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.out.TokenGenerator;
import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenGeneratorImpl implements TokenGenerator {
      private static final Duration ONE_WEEK = Duration.ofDays(7);
      private static final Duration FIVE_MINUTES = Duration.ofSeconds(2);
      private final TokenService tokenService;

      @Override
      public AccessToken generate(UserClaims userClaims) {
            String accessToken = tokenService.encodeToken(userClaims, FIVE_MINUTES);
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
