package com.decade.practice.security.strategy;

import com.decade.practice.core.TokenCredentialService;
import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutStrategy implements LogoutHandler {

      private final TokenCredentialService credentialService;

      public LogoutStrategy(TokenCredentialService credentialService) {
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