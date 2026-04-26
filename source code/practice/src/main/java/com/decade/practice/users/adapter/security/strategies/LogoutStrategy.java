package com.decade.practice.users.adapter.security.strategies;

import com.decade.practice.shared.security.TokenUtils;
import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogoutStrategy implements LogoutHandler {

    private final TokenSessionService tokenSessionService;
    private final TokenGenerator tokenGenerator;


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
        String username = tokenGenerator.decode(refreshToken).username();
        tokenSessionService.logout(username, refreshToken);
    }
}