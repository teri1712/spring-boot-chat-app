package com.decade.practice.users.adapter;

import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import com.decade.practice.users.application.ports.out.TokenGenerator;
import com.decade.practice.users.dto.AccessToken;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenGeneratorImpl implements TokenGenerator {
    private static final Duration ONE_WEEK = Duration.ofDays(7);
    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
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
