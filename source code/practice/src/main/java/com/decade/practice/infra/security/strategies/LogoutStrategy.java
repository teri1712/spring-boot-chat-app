package com.decade.practice.infra.security.strategies;

import com.decade.practice.application.usecases.TokenStore;
import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutStrategy implements LogoutHandler {

    private final TokenStore credentialService;

    public LogoutStrategy(TokenStore credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String refreshToken = TokenUtils.extractRefreshToken(request);
        if (refreshToken == null) {
            return;
        }
        String username = authentication.getName();
        credentialService.evict(username, refreshToken);
    }
}