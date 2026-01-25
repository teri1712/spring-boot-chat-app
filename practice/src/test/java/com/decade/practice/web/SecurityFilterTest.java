package com.decade.practice.web;

import com.decade.practice.common.BaseTestClass;
import com.decade.practice.infra.security.UserClaimsTokenService;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.utils.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SecurityFilterTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserClaimsTokenService tokenService;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenUnauthenticatedUser_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
        // /accounts/me is protected
        mockMvc.perform(get("/accounts/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenUnauthenticatedUser_whenAccessPublicResource_thenReturnsOk() throws Exception {
        // /users is public for registration (POST), but GET might also be public or have different rules.
        // Let's check /medias/** or /users/** (registration)
        // Actually SecurityConfiguration says .requestMatchers("/users/**").permitAll()
        mockMvc.perform(get("/users").param("query", "alice"))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenValidJwtToken_whenAccessProtectedResource_thenReturnsOk() throws Exception {
        // Given
        UserClaims claims = UserClaims.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .username("alice")
                .name("alice")
                .role("ROLE_USER")
                .build();
        String token = tokenService.create(claims, null).getAccessToken();

        // When & Then
        mockMvc.perform(get("/accounts/me")
                        .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenExpiredOrInvalidToken_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/accounts/me")
                        .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}