package com.decade.practice.users.application.services;

import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.User;
import com.decade.practice.users.dto.AccountResponse;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.dto.TokenCredential;
import com.decade.practice.users.dto.mapper.ClaimsMapper;
import com.decade.practice.users.dto.mapper.UserMapper;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TokenSessionServiceImpl implements TokenSessionService {

    private static final long ONE_WEEK = 7L * 24 * 60 * 60 * 1000L;
    private static final long FIFTEEN_MINUTES = 15 * 60 * 1000L;

    private final TokenStore tokenStore;
    private final TokenService tokenService;
    private final UserRepository users;
    private final UserMapper userMapper;
    private final ClaimsMapper claimsMapper;


    private UserClaims validate(String refreshToken) throws AccessDeniedException {
        UserClaims claims;
        try {
            claims = tokenService.decodeToken(refreshToken);
        } catch (Exception e) {
            throw new AccessDeniedException("Token expired", e);
        }
        if (!tokenStore.has(claims.username(), refreshToken)) {
            throw new AccessDeniedException("Token expired");
        }
        return claims;
    }

    @Override
    public String refresh(String refreshToken) throws AccessDeniedException {
        UserClaims claims = validate(refreshToken);
        return tokenService.encodeToken(claims, FIFTEEN_MINUTES);
    }

    @Override
    public void logout(String username, String refreshToken) {
        tokenStore.evict(username, refreshToken);
    }

    @Override
    public AccountResponse login(String username) {
        User user = users.findByUsername(username).orElseThrow();
        ProfileResponse profileResponse = userMapper.toResponse(user);
        UserClaims claims = claimsMapper.toClaims(profileResponse);
        String accessToken = tokenService.encodeToken(claims, FIFTEEN_MINUTES);
        String refresh = tokenService.encodeToken(claims, ONE_WEEK);

        TokenCredential credential = new TokenCredential(
                accessToken,
                refresh
        );
        return new AccountResponse(profileResponse, credential);
    }
}
