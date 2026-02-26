package com.decade.practice.inbox.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.engagement.dto.events.TextIntegrationChatEventPlaced;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.domain.ConversationId;
import com.decade.practice.inbox.domain.HashValue;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ChatListingTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private TestBeans.PrivateChatSender chatSender;

      @Autowired
      private ConversationRepository histories;

      @Autowired
      private ApplicationEvents events;

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
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


            assertThat(events.stream(TextIntegrationChatEventPlaced.class)).hasSize(2);
            assertThat(events.stream(MessageCreated.class)).hasSize(2);
            assertThat(events.stream(InboxLogCreated.class)).hasSize(4);

            // When & Then

            mockMvc.perform(get("/me/conversations")
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(3))
                      .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
                      .andExpect(jsonPath("$[0].messagePreviews.size()").value(1))
                      .andExpect(jsonPath("$[1].identifier").value(aliceBobChat))
                      .andExpect(jsonPath("$[1].messagePreviews.size()").value(1))
            ;
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
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


            assertThat(events.stream(TextIntegrationChatEventPlaced.class)).hasSize(3);
            assertThat(events.stream(MessageCreated.class)).hasSize(3);
            assertThat(events.stream(InboxLogCreated.class)).hasSize(6);


            mockMvc.perform(get("/me/conversations")
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(3))
                      .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
                      .andExpect(jsonPath("$[0].messagePreviews.size()").value(2))
                      .andExpect(jsonPath("$[0].messagePreviews[0].content").value("new dcm"))
                      .andExpect(jsonPath("$[0].messagePreviews[1].content").value("dcm"))
                      .andExpect(jsonPath("$[1].identifier").value(aliceBobChat))
                      .andExpect(jsonPath("$[1].messagePreviews.size()").value(1))
            ;
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
      @WithUserDetails("alice")
      void givenUserListsChatsWithWrongHash_whenListChats_thenReturnsConflictError() throws Exception {
            // Given: Alice has 2 chat

            UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

            String aliceBobChat = aliceId + "+" + bobId;

            chatSender.sendPrivateText("dcm", charlieId, aliceId);
            chatSender.sendPrivateText("vcl", bobId, aliceId);


            assertThat(events.stream(TextIntegrationChatEventPlaced.class)).hasSize(2);
            assertThat(events.stream(MessageCreated.class)).hasSize(2);
            assertThat(events.stream(InboxLogCreated.class)).hasSize(4);

            // When & Then: Request with version 1 should fail
            mockMvc.perform(get("/me/conversations")
                                .queryParam("startAt", aliceBobChat)
                                .queryParam("anchor", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isConflict());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
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


            assertThat(events.stream(TextIntegrationChatEventPlaced.class)).hasSize(7);
            assertThat(events.stream(MessageCreated.class)).hasSize(7);
            assertThat(events.stream(InboxLogCreated.class)).hasSize(13);


            // When & Then: Request with version 1 should fail

            HashValue bobHash = histories.findById(new ConversationId(aliceBobChat, aliceId)).orElseThrow().getHash();
            HashValue charlieHash = histories.findById(new ConversationId(aliceCharlieChat, aliceId)).orElseThrow().getHash();

            mockMvc.perform(get("/me/conversations")
                                .queryParam("startAt", aliceBobChat)
                                .queryParam("anchor", bobHash.value().toString())
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(2))
                      .andExpect(jsonPath("$[0].identifier").value(aliceCharlieChat))
                      .andExpect(jsonPath("$[0].messagePreviews.size()").value(3))
                      .andExpect(jsonPath("$[1].identifier").value(aliceAliceChat))
                      .andExpect(jsonPath("$[1].messagePreviews.size()").value(1));
            mockMvc.perform(get("/me/conversations")
                                .queryParam("startAt", aliceCharlieChat)
                                .queryParam("anchor", charlieHash.value().toString())
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(1))
                      .andExpect(jsonPath("$[0].identifier").value(aliceAliceChat))
                      .andExpect(jsonPath("$[0].messagePreviews.size()").value(1))
            ;
      }

}