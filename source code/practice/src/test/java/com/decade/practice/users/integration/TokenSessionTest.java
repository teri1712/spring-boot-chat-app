package com.decade.practice.users.integration;


import com.decade.practice.BaseTestClass;
import com.decade.practice.users.application.ports.in.ProfileService;
import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.dto.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TokenSessionTest extends BaseTestClass {

      @Autowired
      private TokenStore tokenStore;

      @Autowired
      private TokenSessionService tokenSessionService;

      @Autowired
      private ProfileService profileService;

      @Autowired
      private RedisTemplate<?, ?> redisTemplate;

      @AfterEach
      public void tearDown() {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
      }

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
            assertThat(tokenStore.size("alice")).isEqualTo(1);

            String token2 = tokenSessionService.login("alice").getAccessToken().refreshToken();

            assertThat(tokenStore.has("alice", "token1")).isTrue();
            assertThat(tokenStore.has("alice", token2)).isTrue();
            assertThat(tokenStore.size("alice")).isEqualTo(2);

      }


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      void givenAnActiveSessionOfAlice_whenFirstSessionChangingPassword_thenSessionBecomesInvalidated() {

            AccessToken theSession = tokenSessionService.login("alice").getAccessToken();
            assertThat(tokenStore.size("alice")).isOne();

            profileService.changePassword(UUID.fromString("11111111-1111-1111-1111-111111111111"), "new_password", "Password123!");
            assertThat(tokenStore.size("alice")).isZero();

            AccessToken theNewSession = tokenSessionService.login("alice").getAccessToken();
            assertThatCode(() -> tokenSessionService.refresh(theNewSession.refreshToken()))
                      .doesNotThrowAnyException();
            assertThatThrownBy(() -> tokenSessionService.refresh(theSession.refreshToken()))
                      .isInstanceOf(AccessDeniedException.class);
      }

}
