package com.decade.practice.threads.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.threads.application.ports.out.ChatHistoryRepository;
import com.decade.practice.threads.domain.ChatHistoryId;
import com.decade.practice.threads.domain.HashValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventListingTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestBeans.PrivateChatSender chatSender;

    @Autowired
    private ChatHistoryRepository histories;


    @Autowired
    private ApplicationEvents events;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceHasChats_whenAliceListsChats_thenReturnsAllAliceChats() throws Exception {
        // Given: Alice has 2 chats (with Bob and Charlie)

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("vcl", bobId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);

        // When & Then

        mockMvc.perform(get("/me/chats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chatId").value(aliceCharlieChat))
                .andExpect(jsonPath("$[0].messages.size()").value(1))
                .andExpect(jsonPath("$[1].chatId").value(aliceBobChat))
                .andExpect(jsonPath("$[1].messages.size()").value(1))
        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceHasMultipleChats_whenAliceSendsMessageToOlderChat_thenThatChatMovesToTop() throws Exception {
        // Given
        // Alice has chats with Bob (ID: ...2222) and Charlie (ID: ...3333)

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", bobId, aliceId);


        // When

        chatSender.sendPrivateText("new dcm", charlieId, aliceId);

        mockMvc.perform(get("/me/chats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chatId").value(aliceCharlieChat))
                .andExpect(jsonPath("$[0].messages.size()").value(2))
                .andExpect(jsonPath("$[0].messages[0].content").value("new dcm"))
                .andExpect(jsonPath("$[0].messages[1].content").value("dcm"))
                .andExpect(jsonPath("$[1].chatId").value(aliceBobChat))
                .andExpect(jsonPath("$[1].messages.size()").value(1))
        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenUserListsChatsWithWrongHash_whenListChats_thenReturnsBadRequestError() throws Exception {
        // Given: Alice has 2 chat

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", bobId, aliceId);


        // When & Then: Request with version 1 should fail
        mockMvc.perform(get("/me/chats")
                        .queryParam("startAt", aliceBobChat)
                        .queryParam("hashValue", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenUserListsChatsWithCorrectHash_whenListChats_thenReturnsBadRequestError() throws Exception {
        // Given: Alice has 2 chat

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceAliceChat = aliceId + "+" + aliceId;
        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("meo meo", aliceId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", bobId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", bobId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", bobId, aliceId);

        // When & Then: Request with version 1 should fail

        HashValue bobHash = histories.findById(new ChatHistoryId(aliceBobChat, aliceId)).orElseThrow().getHash();
        HashValue charlieHash = histories.findById(new ChatHistoryId(aliceCharlieChat, aliceId)).orElseThrow().getHash();

        mockMvc.perform(get("/me/chats")
                        .queryParam("startAt", aliceBobChat)
                        .queryParam("hashValue", bobHash.value().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chatId").value(aliceCharlieChat))
                .andExpect(jsonPath("$[0].messages.size()").value(3))
                .andExpect(jsonPath("$[1].chatId").value(aliceAliceChat))
                .andExpect(jsonPath("$[1].messages.size()").value(1));
        mockMvc.perform(get("/me/chats")
                        .queryParam("startAt", aliceCharlieChat)
                        .queryParam("hashValue", charlieHash.value().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].chatId").value(aliceAliceChat))
                .andExpect(jsonPath("$[0].messages.size()").value(1))
        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenChatHasEvents_whenAliceListsEventsForChat_thenReturnsEventsOrderedByVersionDesc() throws Exception {
        // Given

        mockMvc.perform(get("/chats")
                        .param("partnerId", "22222222-2222-2222-2222-222222222222")
                )
                .andExpect(status().isCreated());

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("meo meo", bobId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", aliceId, bobId);


        // When & Then
        // After 2 messages, Alice's eventVersion should be 2 (if it started at 0 and incVersion was called twice)
        mockMvc.perform(get("/chats/{chatId}/events", aliceBobChat)
                        .param("atVersion", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("vcl"))
                .andExpect(jsonPath("$[1].content").value("meo meo"));

        mockMvc.perform(get("/chats/{chatId}/events", aliceBobChat)
                        .param("atVersion", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("meo meo"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenUserHasEvents_whenAliceListsEvents_thenReturnsAllHerEvents() throws Exception {
        // Given
        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String aliceBobChat = aliceId + "+" + bobId;
        String aliceCharlieChat = aliceId + "+" + charlieId;

        chatSender.sendPrivateText("meo meo", bobId, aliceId);
        chatSender.sendPrivateText("dcm", charlieId, aliceId);
        chatSender.sendPrivateText("vcl", aliceId, aliceId);


        // When & Then
        mockMvc.perform(get("/users/me/events")
                        .param("atVersion", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}