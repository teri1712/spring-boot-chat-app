package com.decade.practice.users.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.TokenUtils;
import com.decade.practice.shared.security.UserClaims;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentTest(datasets = {UserDataset.class, RedisDataset.class})
@RequiredArgsConstructor
class UserFilterTest {
    final MockMvc mockMvc;
    final TokenService tokenService;
    final ObjectMapper objectMapper;
    final UserRepository users;

    @BeforeEach
    void signUpAlice() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("alice");
        request.setPassword("Password123!");
        request.setName("Alice Liddell");
        request.setGender(1.0f);
        request.setDob(Instant.now());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

    }

    @Test
    void givenUnauthenticatedUser_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
        // /profiles/me is protected
        mockMvc.perform(get("/profiles/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidJwtToken_whenAccessProtectedResource_thenReturnsOk() throws Exception {
        // Given
        UUID aliceId = users.findByUsername("alice").orElseThrow().getId();
        UserClaims claims = new UserClaims
            (aliceId,
                "alice",
                "alice",
                "luffy.jpg");
        String token = tokenService.encodeToken(claims, Duration.ofDays(5));

        // When & Then
        mockMvc.perform(get("/profiles/me")
                .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " " + token))
            .andExpect(status().isOk());
    }

    @Test
    void givenExpiredOrInvalidToken_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/profiles/me")
                .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " vcl-token"))
            .andExpect(status().isUnauthorized());
    }
}
