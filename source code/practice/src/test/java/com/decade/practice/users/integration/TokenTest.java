package com.decade.practice.users.integration;


import com.decade.practice.BaseTestClass;
import com.decade.practice.users.application.ports.in.ProfileService;
import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.dto.AccountResponse;
import com.decade.practice.web.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TokenTest extends BaseTestClass {

      @Autowired
      private TokenStore tokenStore;

      @Autowired
      private TokenService tokenService;

      @Autowired
      private TokenSessionService tokenSessionService;

      @Autowired
      private ProfileService profileService;

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void given2ActiveSessionsOfAlice_whenLogOutTheFirstOne_thenOnlyTheSecondRemainsActive() {
            // given
            tokenStore.add("alice", "token1");
            tokenStore.add("alice", "token2");

            tokenSessionService.logout("alice", "token1");

            assertThat(tokenStore.has("alice", "token2")).isTrue();
            assertThat(tokenStore.has("alice", "token1")).isFalse();
            assertThat(tokenStore.size("alice")).isEqualTo(1);

      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void given1ActiveSessionsOfAlice_whenLoginIn_thenTwoSessionsAreActive() {
            // given
            tokenStore.add("alice", "token1");

            String token2 = tokenSessionService.login("alice").getAccessToken().refreshToken();

            assertThat(tokenStore.has("alice", "token1")).isTrue();
            assertThat(tokenStore.has("alice", token2)).isTrue();
            assertThat(tokenStore.size("alice")).isEqualTo(2);

      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void given2ActiveSessionsOfAlice_whenFirstSessionChangingPassword_thenTwoSessionsBecomesInvalidated() {
            // given
            tokenStore.add("alice", "token1");
            tokenStore.add("alice", "token2");

            AccountResponse account = profileService.changePassword(UUID.fromString("11111111-1111-1111-1111-111111111111"), "new_password", "Password123!");
            assertThat(tokenStore.size("alice")).isOne();

            assertThat(tokenStore.has("alice", "token1")).isFalse();
            assertThat(tokenStore.has("alice", "token2")).isFalse();

            assertThat(tokenStore.has("alice", account.getAccessToken().refreshToken())).isTrue();
      }

}
