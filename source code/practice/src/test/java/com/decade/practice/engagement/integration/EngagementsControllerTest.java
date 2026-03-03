package com.decade.practice.engagement.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.domain.events.*;
import com.decade.practice.engagement.dto.PreferenceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EngagementsControllerTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private ApplicationEvents events;

      @Autowired
      private ObjectMapper objectMapper;

      @Autowired
      private EngagementService engagementService;


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenAliceAndBobChatNotExists_whenAliceGetChatWithBob_thenCreated() throws Exception {
            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isOk());

            String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            mockMvc.perform(get("/chats/{id}", chatId))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.identifier").value(chatId));
      }


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenValidTextEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
            // Given
            // Create chat first
            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "content": "Hello Bob"
                      }
                      """;

            // When & Then
            String idempotentKey = UUID.randomUUID().toString();
            mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatIdentifier)
                                .header("Idempotency-key", idempotentKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted())
                      .andExpect(jsonPath("$.id").value(idempotentKey));


            assertThat(events.stream(TextChatEventAccepted.class)).hasSize(1);


            // Idempotent check
            mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatIdentifier)
                                .header("Idempotency-key", idempotentKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.id").value(idempotentKey));

      }


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
      @WithUserDetails("alice")
      void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
            // Given
            // Correct order: 1111... < 2222...
            // Create chat first
            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            PreferenceRequest preference = new PreferenceRequest(99, "My pookie bob", 99L, null);

            // When
            mockMvc.perform(patch("/chats/{id}/preference", chatId)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(preference)))
                      .andExpect(status().isAccepted());

            // Then
            mockMvc.perform(get("/chats/{id}", chatId))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.preference.roomName").value("My pookie bob"))
                      .andExpect(jsonPath("$.preference.iconId").value(99));


            assertThat(events.stream(PreferenceChatEventAccepted.class)).hasSize(1);

      }


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenValidImageEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
            // Given
            // Create chat first
            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "uri": "http://example.com/image.jpg",
                          "width": 200,
                          "height": 200,
                          "filename": "vcl.jpg",
                          "format": "jpg"
                      }
                      """;

            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/image-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted());

            assertThat(events.stream(ImageChatEventAccepted.class)).hasSize(1);
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenValidIconEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
            // Given

            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "iconId": 5
                      }
                      """;
            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/icon-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted());


            assertThat(events.stream(IconChatEventAccepted.class)).hasSize(1);
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenValidSeenEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
            // Given

            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "at": "2016-01-24T10:15:30Z"
                      }
                      """;

            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/seen-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted());


            assertThat(events.stream(SeenChatEventAccepted.class)).hasSize(1);
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenValidFileEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
            // Given

            mockMvc.perform(post("/chats")
                                .param("partnerId", "22222222-2222-2222-2222-222222222222")
                      )
                      .andExpect(status().isCreated());

            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "filename": "test.txt",
                          "size": 1024,
                          "uri": "http://example.com/test.txt"
                      }
                      """;
            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/file-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())

                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isAccepted());


            assertThat(events.stream(FileChatEventAccepted.class)).hasSize(1);
      }


      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenAliceRequestChatWithNonExistentUser_whenGetChat_thenReturnsNotFoundError() throws Exception {
            // Given: Alice exists, but we request a chat with a random UUID
            String randomUser = UUID.randomUUID().toString();
            String chatId = "11111111-1111-1111-1111-111111111111+" + randomUser;

            // When & Then: getOrCreateChat will fail to find the second user
            mockMvc.perform(get("/chats/{id}", chatId))
                      .andExpect(status().isNotFound());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenUserNotPartOfChat_whenGetChat_thenReturnsForbidden() throws Exception {
            // Given: Alice is not part of Bob-Charlie chat

            engagementService.getOrCreate(UUID.fromString("22222222-2222-2222-2222-222222222222"), UUID.fromString("33333333-3333-3333-3333-333333333333"));

            // Let's assume Bob-Charlie chat is created.
            String bobCharlieChat = "22222222-2222-2222-2222-222222222222+33333333-3333-3333-3333-333333333333";

            // When & Then: Alice can't still request it
            mockMvc.perform(get("/chats/{id}", bobCharlieChat)
                                .param("anchorSequenceNumber", "0"))
                      .andExpect(status().isForbidden());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("charlie")
      void givenCharlieSendsToAliceBobChat_whenCreateTextEvent_thenReturnsForbidden() throws Exception {
            // Given: Charlie is not part of Alice-Bob chat

            engagementService.getOrCreate(UUID.fromString("22222222-2222-2222-2222-222222222222"), UUID.fromString("11111111-1111-1111-1111-111111111111"));


            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String eventJson = """
                      {
                          "content": "Intruding messagePreview"
                      }
                      """;

            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(eventJson))
                      .andExpect(status().isForbidden());
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenMalformedJson_whenCreateTextEvent_thenReturnsBadRequest() throws Exception {
            // Given
            String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
            String malformedJson = "{ \"textEvent\": { ... } }";

            // When & Then
            mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatIdentifier)
                                .header("Idempotency-key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(malformedJson))
                      .andExpect(status().isBadRequest());
      }
}
