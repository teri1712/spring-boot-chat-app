package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.dto.MessageStateResponse;
import com.decade.practice.shared.security.jwt.WithJwtUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor
@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
class MessageListingTest extends BaseInboxTestClass {

    final ObjectMapper objectMapper;
    final ConversationApi conversationApi;

    @Test
    void givenChatHasLogs_whenAliceListsLogsForChat_thenReturnsLogsOrderedBySequenceIdDesc() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), null);

        sendText(aliceBobChat, "meomeo");
        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");
        sendText(aliceBobChat, "dcm");
        sendText(aliceBobChat, "dcm");


        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(10);
            });

        // When & Then
        String bodyString = mockMvc.perform(get("/chats/{chatId}/messages", aliceBobChat)
                .param("anchorSequenceNumber", String.valueOf(Long.MAX_VALUE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].content").value("dcm"))
            .andExpect(jsonPath("$[1].content").value("dcm"))
            .andExpect(jsonPath("$[2].content").value("vcl"))
            .andExpect(jsonPath("$[3].content").value("meomeo"))
            .andReturn().getResponse().getContentAsString();

        List<MessageStateResponse> messages = objectMapper.readValue(bodyString, new TypeReference<>() {
        });


        mockMvc.perform(get("/chats/{chatId}/messages", aliceBobChat)
                .param("anchorSequenceNumber", messages.get(1).getSequenceNumber().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].content").value("dcm"))
            .andExpect(jsonPath("$[1].content").value("vcl"))
            .andExpect(jsonPath("$[2].content").value("meomeo"))

        ;
    }

}
