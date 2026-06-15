package com.decade.practice.chat.integration;

import com.decade.practice.chatsettings.integration.SettingDataset;
import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.engagement.integration.EngagementDataset;
import com.decade.practice.inbox.integration.InboxDataset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@TestPropertySource(properties = {
    "redis.cache.enabled=true"
})

@ComponentTest(datasets = {InboxDataset.class, SettingDataset.class, EngagementDataset.class})
@RequiredArgsConstructor
class DirectCacheTest {

    final MockMvc mockMvc;

    @Test
    @WithJwtUser
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
