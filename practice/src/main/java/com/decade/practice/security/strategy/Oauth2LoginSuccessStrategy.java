package com.decade.practice.security.strategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Oauth2LoginSuccessStrategy implements AuthenticationSuccessHandler {

      private final String frontEnd;

      public Oauth2LoginSuccessStrategy(@Value("${front-end}") String frontEnd) {
            this.frontEnd = frontEnd;
      }

      @Override
      public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
      ) throws IOException {
            response.sendRedirect(frontEnd);
      }
}