package com.decade.practice.security.strategy;

import com.decade.practice.utils.PlatformsUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class EntryPointStrategy implements AuthenticationEntryPoint {

      @Override
      public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
            if (PlatformsUtils.isBrowserNavigation(request)) {
                  response.sendRedirect("/login");
            } else {
                  response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            }
      }
}
