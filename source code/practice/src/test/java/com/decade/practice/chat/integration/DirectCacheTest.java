package com.decade.practice.chat.integration;

import com.decade.practice.BaseTestClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@TestPropertySource(properties = {
          "spring.profiles.active=dev,redis-cache",
          "logging.level.root=INFO"
})
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class DirectCacheTest extends BaseTestClass {

      @Autowired
      MockMvc mockMvc;

      @Test
      @WithUserDetails("alice")
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
      void givenFirstCallSuccess_whenSecondCallMade_returnTheCachedOne() throws Exception {

            mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222"))
                      .andExpect(status().isCreated());

            Instant now = Instant.now();
            mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222"))
                      .andExpect(status().isOk());
            Duration before = Duration.between(now, Instant.now());

            mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222"))
                      .andExpect(status().isOk());

            now = Instant.now();
            Duration after = Duration.between(now, Instant.now());

            assertThat(after).isLessThan(before);
      }
}
