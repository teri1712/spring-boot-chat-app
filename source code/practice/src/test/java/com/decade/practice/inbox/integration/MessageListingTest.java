package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.dto.MessageStateResponse;
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
class MessageListingTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestBeans.PrivateChatSender chatSender;

    @Autowired
    private ObjectMapper objectMapper;

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
        chatSender.emitText("dcm", bobId, aliceId);
        chatSender.emitText("dcm", bobId, aliceId);


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