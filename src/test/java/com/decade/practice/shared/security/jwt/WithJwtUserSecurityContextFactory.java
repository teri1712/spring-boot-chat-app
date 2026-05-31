package com.decade.practice.shared.security.jwt;

import com.decade.practice.shared.security.UserClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Duration;
import java.util.UUID;

public class WithJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithJwtUser> {

    private final JwtService jwtService = new JwtService("vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl");

    @Override
    public SecurityContext createSecurityContext(WithJwtUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserClaims claims = new UserClaims(
            UUID.fromString(annotation.id()),
            annotation.username(),
            annotation.name(),
            annotation.avatar()
        );

        String token = jwtService.encodeToken(claims, Duration.ofDays(1));
        JwtUser jwtUser = new JwtUser(claims);
        Authentication auth = new JwtUserAuthentication(jwtUser, token);
        context.setAuthentication(auth);
        return context;
    }
}
