package com.decade.practice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class FormLoginFailHandler implements AuthenticationFailureHandler {

      @Override
      public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
            String message;
            if (exception instanceof UsernameNotFoundException) {
                  message = "Username not found";
            } else {
                  message = "Wrong password";
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(message);
            response.getWriter().flush();
      }

}
