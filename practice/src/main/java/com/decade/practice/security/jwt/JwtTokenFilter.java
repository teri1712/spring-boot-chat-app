package com.decade.practice.security.jwt;

import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtTokenFilter extends OncePerRequestFilter {

      private final JwtCredentialService jwtCredentialService;

      public JwtTokenFilter(JwtCredentialService jwtCredentialService) {
            this.jwtCredentialService = jwtCredentialService;
      }

      @Override
      protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
      ) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                  filterChain.doFilter(request, response);
                  return;
            }

            try {
                  String accessToken = TokenUtils.extractToken(request);
                  if (accessToken != null) {
                        var claims = jwtCredentialService.decodeToken(accessToken);
                        var principal = new JwtUser(claims);
                        var context = SecurityContextHolder.createEmptyContext();
                        var authentication = new JwtUserAuthentication(principal, accessToken);
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);
                        // NOTE: For token-based authentication, will not be saved into security context repository
                  }
                  filterChain.doFilter(request, response);
            } catch (Exception e) {
                  e.printStackTrace();
                  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
      }
}