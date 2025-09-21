package com.decade.practice.adapter.web.rest;

import com.decade.practice.adapter.security.UserClaimsTokenService;
import com.decade.practice.adapter.security.models.UserClaims;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.TokenCredential;
import com.decade.practice.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/tokens")
public class TokenController {

        private final UserService userService;
        private final UserClaimsTokenService userClaimsTokenService;

        public TokenController(
                UserService userService,
                UserClaimsTokenService userClaimsTokenService
        ) {
                this.userService = userService;
                this.userClaimsTokenService = userClaimsTokenService;
        }

        @PostMapping("/oauth2")
        public void oidcLogin(
                @AuthenticationPrincipal Jwt jwt,
                HttpServletRequest request,
                HttpServletResponse response
        ) throws Exception {
                String username = jwt.getSubject();

                var claims = jwt.getClaims();
                String name = claims.get("name").toString();
                String picture = claims.get("picture").toString();
                try {
                        userService.createOauth2User(username, name, picture);
                } catch (DataIntegrityViolationException ignored) {
                }

                request.getRequestDispatcher("/account").forward(request, response);
        }

        @PostMapping("/refresh")
        public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
                String refreshToken = TokenUtils.extractRefreshToken(request);
                if (refreshToken == null) {
                        throw new AccessDeniedException("No refresh token provided in the request");
                }

                userClaimsTokenService.validate(refreshToken);

                UserClaims claims = userClaimsTokenService.decodeToken(refreshToken);
                TokenCredential credential = userClaimsTokenService.create(claims, refreshToken);

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(
                        new ObjectMapper()
                                .enable(SerializationFeature.INDENT_OUTPUT)
                                .writeValueAsString(credential)
                );
                response.getWriter().flush();
        }
}