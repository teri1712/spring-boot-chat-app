package com.decade.practice.cache;

import com.decade.practice.common.BaseTestClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(properties = {
        "server.cache.events=true"
})
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EventCacheTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Sql({"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql", "/sql/seed_events.sql"})
    @WithUserDetails("alice")
    public void givenAliceHasEvents_whenAliceQueryListing_thenReturnEventsAndAreCached() throws Exception {
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

        log.trace("Calling get events for Chat Id: {}", chatId);
        Instant start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        Instant end = Instant.now();
        log.trace("Fetched events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

        log.trace("Calling get events after events already cached for Chat Id: {}", chatId);
        start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        end = Instant.now();
        log.trace("Fetched cached events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

    }

    @Test
    @Sql({"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql", "/sql/seed_events.sql"})
    @WithUserDetails("alice")
    public void givenEventsOnCached_whenAliceSendNewMessage_thenReturnEventsAreNotInCached() throws Exception {
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"To Bob\" }"))
                .andExpect(status().isCreated());

        Instant start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$[0].textEvent.content").value("To Bob"));

        Instant end = Instant.now();
        log.trace("Fetched events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

        log.trace("Calling get events after events already cached for Chat Id: {}", chatId);
        start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        end = Instant.now();
        log.trace("Fetched cached events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

    }


}
