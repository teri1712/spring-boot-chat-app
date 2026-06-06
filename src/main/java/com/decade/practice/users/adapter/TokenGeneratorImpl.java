package com.decade.practice.users.adapter;

import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import com.decade.practice.users.application.ports.out.TokenGenerator;
import com.decade.practice.users.dto.AccessToken;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenGeneratorImpl implements TokenGenerator {

    @Value("${token.access.duration}")
    Duration accessDuration;

    @Value("${token.refresh.duration}")
    Duration refreshDuration;
    final TokenService tokenService;


    @Override
    public AccessToken generate(UserClaims userClaims) {
        String accessToken = tokenService.encodeToken(userClaims, accessDuration);
        String refreshToken = generateRefreshToken(userClaims);
        return new AccessToken(accessToken, refreshToken);
    }

    @Override
    public String generateRefreshToken(UserClaims userClaims) {
        return tokenService.encodeToken(userClaims, refreshDuration);
    }

    @Override
    public UserClaims decode(String token) throws JwtException {
        return tokenService.decodeToken(token);
    }
}
