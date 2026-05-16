package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.dto.InboxLogWithPartnerDto;
import com.decade.practice.shared.security.jwt.WithJwtUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor
@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
class LogListingTest extends BaseInboxTestClass {

    final ObjectMapper objectMapper;
    final ConversationApi conversationApi;

    @Test
    void givenChatHasLogs_whenAliceListsLogsForChat_thenReturnsLogsOrderedBySequenceIdDesc() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        sendText(aliceBobChat, "meomeo");
        sendText(aliceBobChat, "vcl");

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(4);
            });

        // When & Then
        String bodyString = mockMvc.perform(get("/chats/{chatId}/logs", aliceBobChat)
                .param("anchorSequenceNumber", String.valueOf(Long.MIN_VALUE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].messageState.content").value("meomeo"))
            .andExpect(jsonPath("$[1].messageState.content").value("vcl"))
            .andReturn().getResponse().getContentAsString();

        List<InboxLogWithPartnerDto> logs = objectMapper.readValue(bodyString, new TypeReference<>() {
        });


        mockMvc.perform(get("/chats/{chatId}/logs", aliceBobChat)
                .param("anchorSequenceNumber", logs.get(1).sequenceNumber().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].messageState.content").value("vcl"));
    }

    @Test
    void givenUserHasLogs_whenAliceListsLogs_thenReturnsAllHerLogs() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), null);
        conversationApi.create(aliceAliceChat, aliceId, Set.of(aliceId), null);

        sendText(aliceBobChat, "meomeo");
        sendText(aliceCharlieChat, "dcm");
        sendText(aliceAliceChat, "vcl");

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(5);
            });

        // When & Then
        String bodyString = mockMvc.perform(get("/users/me/logs")
                .param("anchorSequenceNumber", String.valueOf(Long.MIN_VALUE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].messageState.content").value("meomeo"))
            .andExpect(jsonPath("$[1].messageState.content").value("dcm"))
            .andExpect(jsonPath("$[2].messageState.content").value("vcl"))
            .andReturn().getResponse().getContentAsString();

        List<InboxLogWithPartnerDto> logs = objectMapper.readValue(bodyString, new TypeReference<>() {
        });
        mockMvc.perform(get("/users/me/logs")
                .param("anchorSequenceNumber", logs.get(1).sequenceNumber().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].messageState.content").value("dcm"))
            .andExpect(jsonPath("$[1].messageState.content").value("vcl"))

        ;
    }

}
