package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.domain.HashValue;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.shared.security.jwt.WithJwtUser;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;
import java.util.UUID;
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
class ConversationListingTest extends BaseInboxTestClass {

    final ConversationRepository conversations;
    final ConversationApi conversationApi;

    @Test
    void givenAliceHasChats_whenAliceListsChats_thenReturnsAllAliceChats() throws Exception {
        // Given: Alice has 2 chats (with Bob and Charlie)

        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), "alice & charlie");

        sendText(aliceBobChat, "hello bob");
        sendText(aliceCharlieChat, "hello charlie");

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(4);
            });

        // When & Then

        mockMvc.perform(get("/conversations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
            .andExpect(jsonPath("$[0].recents.size()").value(1))
            .andExpect(jsonPath("$[0].roomName").value("alice & charlie"))
            .andExpect(jsonPath("$[0].recents[0].content").value("hello charlie"))
            .andExpect(jsonPath("$[1].identifier").value(aliceBobChat))
            .andExpect(jsonPath("$[1].recents.size()").value(1))
            .andExpect(jsonPath("$[1].roomName").value("Bob Builder"))
            .andExpect(jsonPath("$[1].recents[0].content").value("hello bob"))
        ;

    }

    @Test
    void givenAliceHasMultipleChats_whenAliceSendsMessageToOlderChat_thenThatChatMovesToTop() throws Exception {
        // Given
        // Alice has chats with Bob (ID: ...2222) and Charlie (ID: ...3333)

        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), "alice & charlie");

        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");

        // When

        sendText(aliceCharlieChat, "new dcm");


        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(6);
            });


        mockMvc.perform(get("/conversations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
            .andExpect(jsonPath("$[0].roomName").value("alice & charlie"))
            .andExpect(jsonPath("$[0].recents.size()").value(2))
            .andExpect(jsonPath("$[0].recents[0].content").value("new dcm"))
            .andExpect(jsonPath("$[0].recents[0].sender.id").exists())
            .andExpect(jsonPath("$[0].recents[1].content").value("dcm"))
            .andExpect(jsonPath("$[0].recents[1].sender.id").exists())
            .andExpect(jsonPath("$[1].identifier").value(aliceBobChat))
            .andExpect(jsonPath("$[1].roomName").value("Bob Builder"))
            .andExpect(jsonPath("$[1].recents.size()").value(1))
        ;
    }

    @Test
    void givenUserListsChatsWithWrongRevision_whenListChats_thenReturnsNotFoundError() throws Exception {
        // Given: Alice has 2 chat

        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), "alice & charlie");

        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");


        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(4);
            });


        // When & Then: Request with version 1 should fail
        mockMvc.perform(get("/conversations")
                .queryParam("anchorRevisionNumber", "-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void givenUserListsChatsWithCorrectRevision_whenListChats_thenReturnsTheListAnchorByThatRevision() throws Exception {
        // Given: Alice has 2 chat

        conversationApi.create(aliceAliceChat, aliceId, Set.of(aliceId), null);
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);
        conversationApi.create(aliceCharlieChat, aliceId, Set.of(aliceId, charlieId), "alice & charlie");


        sendText(aliceAliceChat, "meomeo");
        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");
        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");
        sendText(aliceCharlieChat, "dcm");
        sendText(aliceBobChat, "vcl");

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(InboxLogCreated.class)).hasSize(13);
            });


        // When & Then: Request with version 1 should fail

        HashValue bobHash = conversations.findByChatIdAndOwnerId(aliceBobChat, aliceId).orElseThrow().conversation().getHash();
        HashValue charlieHash = conversations.findByChatIdAndOwnerId(aliceCharlieChat, aliceId).orElseThrow().conversation().getHash();

        mockMvc.perform(get("/conversations")
                .queryParam("anchorRevisionNumber", bobHash.value().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].identifier").value(aliceBobChat))
            .andExpect(jsonPath("$[0].recents.size()").value(3))
            .andExpect(jsonPath("$[1].identifier").value(aliceCharlieChat))
            .andExpect(jsonPath("$[1].recents.size()").value(3));

        mockMvc.perform(get("/conversations")
                .queryParam("anchorRevisionNumber", charlieHash.value().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
            .andExpect(jsonPath("$[0].recents.size()").value(3))
        ;
    }

}
