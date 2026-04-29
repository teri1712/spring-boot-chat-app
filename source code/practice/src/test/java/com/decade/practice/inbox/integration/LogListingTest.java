package com.decade.practice.inbox.integration;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.integration.TestBeans;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LogListingTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestBeans.PrivateChatSender chatSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenChatHasLogs_whenAliceListsLogsForChat_thenReturnsLogsOrderedBySequenceIdDesc() throws Exception {
        // Given

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.emitText("meomeo", bobId, aliceId);
        chatSender.emitText("dcm", charlieId, aliceId);
        chatSender.emitText("vcl", aliceId, bobId);


        // When & Then
        // After 2 messages, Alice's eventVersion should be 2 (if it started at 0 and incVersion was called twice)
        String bodyString = mockMvc.perform(get("/chats/{chatId}/logs", aliceBobChat)
                .param("anchorSequenceNumber", String.valueOf(Long.MIN_VALUE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].messageState.content").value("meomeo"))
            .andExpect(jsonPath("$[1].messageState.content").value("vcl"))
            .andReturn().getResponse().getContentAsString();

        List<InboxLogResponse> logs = objectMapper.readValue(bodyString, new TypeReference<>() {
        });


        mockMvc.perform(get("/chats/{chatId}/logs", aliceBobChat)
                .param("anchorSequenceNumber", logs.get(1).sequenceNumber().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].messageState.content").value("vcl"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenUserHasLogs_whenAliceListsLogs_thenReturnsAllHerLogs() throws Exception {
        // Given
        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatService.getDirect(aliceId, bobId);
        chatService.getDirect(aliceId, charlieId);
        chatService.getDirect(aliceId, aliceId);

        chatSender.emitText("meomeo", bobId, aliceId);
        chatSender.emitText("dcm", charlieId, aliceId);
        chatSender.emitText("vcl", aliceId, aliceId);


        // When & Then
        String bodyString = mockMvc.perform(get("/users/me/logs")
                .param("anchorSequenceNumber", String.valueOf(Long.MIN_VALUE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].messageState.content").value("meomeo"))
            .andExpect(jsonPath("$[1].messageState.content").value("dcm"))
            .andExpect(jsonPath("$[2].messageState.content").value("vcl"))
            .andReturn().getResponse().getContentAsString();

        List<InboxLogResponse> logs = objectMapper.readValue(bodyString, new TypeReference<>() {
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