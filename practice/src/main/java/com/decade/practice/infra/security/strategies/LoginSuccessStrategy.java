package com.decade.practice.infra.security.strategies;

import com.decade.practice.application.usecases.UserService;
import com.decade.practice.dto.AccountEntryResponse;
import com.decade.practice.dto.AccountResponse;
import com.decade.practice.dto.TokenCredential;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.infra.security.UserClaimsService;
import com.decade.practice.infra.security.models.UserClaims;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class LoginSuccessStrategy implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final UserClaimsService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication
    ) throws IOException {
        AccountResponse account = userService.prepareAccount(authentication.getName());
        UserResponse userDto = account.getUser();
        TokenCredential tokenCredential = tokenService.create(UserClaims.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .name(userDto.getName())
                .role(userDto.getRole())
                .gender(userDto.getGender())
                .avatar(userDto.getAvatar())
                .build(), null);
        AccountEntryResponse entryResponse = new AccountEntryResponse(account, tokenCredential);
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.getWriter().write(objectMapper.writeValueAsString(entryResponse));
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.getWriter().flush();
    }
}
