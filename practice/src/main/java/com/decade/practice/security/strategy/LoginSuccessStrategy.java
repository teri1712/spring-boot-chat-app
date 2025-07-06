package com.decade.practice.security.strategy;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.core.UserOperations;
import com.decade.practice.utils.PlatformsUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LoginSuccessStrategy implements AuthenticationSuccessHandler {

      private final UserOperations userOperations;
      private final ChatOperations chatOperations;

      public LoginSuccessStrategy(UserOperations userOperations, ChatOperations chatOperations) {
            this.userOperations = userOperations;
            this.chatOperations = chatOperations;
      }

      @Override
      public void onAuthenticationSuccess(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication
      ) throws IOException {

            if (PlatformsUtils.isBrowserNavigation(httpRequest)) {
                  httpResponse.sendRedirect("/profile");
                  return;
            }

            try {
                  httpRequest.getRequestDispatcher("/account/login").forward(httpRequest, httpResponse);
            } catch (ServletException e) {
                  e.printStackTrace();
            }

      }
}
