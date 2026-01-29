package com.decade.practice.infra.security.jwt;

import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.utils.TokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String accessToken = TokenUtils.extractToken(request);
            if (accessToken != null) {
                logger.debug("Found access token for request");
                UserClaims claims = jwtService.decodeToken(accessToken);
                JwtUser principal = new JwtUser(claims);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authentication = new JwtUserAuthentication(principal, accessToken);
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } else {
                logger.debug("No access token found in request " + request.getRequestURI());
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            logger.warn("Invalid access token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}