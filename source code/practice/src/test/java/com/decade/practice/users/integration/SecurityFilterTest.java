package com.decade.practice.users.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.TokenUtils;
import com.decade.practice.web.security.UserClaims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SecurityFilterTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private TokenService tokenService;


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void givenUnauthenticatedUser_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
            // /profiles/me is protected
            mockMvc.perform(get("/profiles/me"))
                      .andExpect(status().isUnauthorized());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void givenUnauthenticatedUser_whenAccessPublicResource_thenReturnsOk() throws Exception {
            // /users is public for registration (POST), but GET might also be public or have different rules.
            // Let's check /medias/** or /users/** (registration)
            // Actually SecurityConfiguration says .requestMatchers("/users/**").permitAll()
            mockMvc.perform(get("/swagger-ui/index.html"))
                      .andExpect(status().isOk());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void givenValidJwtToken_whenAccessProtectedResource_thenReturnsOk() throws Exception {
            // Given
            UserClaims claims = new UserClaims
                      (UUID.fromString("11111111-1111-1111-1111-111111111111"),
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
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void givenExpiredOrInvalidToken_whenAccessProtectedResource_thenReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/profiles/me")
                                .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " vcl-token"))
                      .andExpect(status().isUnauthorized());
      }
}