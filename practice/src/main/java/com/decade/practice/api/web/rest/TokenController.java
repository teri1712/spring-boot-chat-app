package com.decade.practice.api.web.rest;

import com.decade.practice.api.dto.SignUpRequest;
import com.decade.practice.api.dto.TokenCredential;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.infra.security.UserClaimsTokenService;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.infra.security.strategies.LoginSuccessStrategy;
import com.decade.practice.persistence.jpa.DefaultAvatar;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/tokens")
public class TokenController {

    private final UserService userService;
    private final LoginSuccessStrategy loginSuccessStrategy;
    private final UserClaimsTokenService userClaimsTokenService;

    @PostMapping("/oauth2")
    public void exchange(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        String username = jwt.getSubject();

        var claims = jwt.getClaims();
        String name = claims.get("name").toString();
        String picture = claims.get("picture").toString();

        ImageSpec avatar = (picture != null)
                ? new ImageSpec(picture, picture, ImageSpec.DEFAULT_WIDTH, ImageSpec.DEFAULT_HEIGHT, ImageSpec.DEFAULT_FORMAT)
                : DefaultAvatar.getInstance();
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(username);
        signUpRequest.setName(name);
        signUpRequest.setDob(new Date());
        signUpRequest.setGender(User.MALE);
        signUpRequest.setAvatar(avatar);
        signUpRequest.setPassword(UUID.randomUUID().toString());
        try {
            userService.create(signUpRequest, false);
        } catch (DataIntegrityViolationException ignored) {
            log.debug("Oauth2 user already exists");
        }
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.getContextHolderStrategy().setContext(securityContext);
        loginSuccessStrategy.onAuthenticationSuccess(request, response, authenticationToken);
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