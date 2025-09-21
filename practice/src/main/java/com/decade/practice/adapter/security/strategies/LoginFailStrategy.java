package com.decade.practice.adapter.security.strategies;

import com.decade.practice.utils.PlatformsUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginFailStrategy implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException exception
        ) throws IOException {
                String message = (exception instanceof UsernameNotFoundException)
                        ? "Username not found"
                        : "Wrong password";
                exception.printStackTrace();
                if (!PlatformsUtils.isBrowserNavigation(request)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("text/plain;charset=UTF-8");
                        response.getWriter().write(message);
                        response.getWriter().flush();
                } else {
                        response.sendRedirect("/login?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));
                }
        }
}