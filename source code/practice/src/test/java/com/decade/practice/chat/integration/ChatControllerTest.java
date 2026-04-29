package com.decade.practice.chat.integration;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.RoomCreated;
import com.decade.practice.integration.BaseTestClass;
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
class ChatControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;


    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceAndBobChatNotExists_whenAliceGetChatWithBob_thenCreated() throws Exception {
        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
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

        chatService.getDirect(UUID.fromString("22222222-2222-2222-2222-222222222222"), UUID.fromString("33333333-3333-3333-3333-333333333333"));

        // Let's assume Bob-Charlie chat is created.
        String bobCharlieChat = "22222222-2222-2222-2222-222222222222+33333333-3333-3333-3333-333333333333";

        // When & Then: Alice can't still request it
        mockMvc.perform(get("/chats/{id}", bobCharlieChat)
                .param("anchorSequenceNumber", "0"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
    @WithUserDetails("alice")
    void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
        // Given
        // Correct order: 1111... < 2222...
        // Create chat first
        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        PreferenceRequest preference = new PreferenceRequest(99, "My pookie bob", 3L, "Vcl");

        // When
        mockMvc.perform(patch("/chats/{id}/preference", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preference)))
            .andExpect(status().isAccepted());

        assertThat(events.stream(PreferenceChanged.class)).hasSize(1);

        // Then
        mockMvc.perform(get("/chats/{id}", chatId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.preference.customName").value("My pookie bob"))
            .andExpect(jsonPath("$.preference.iconId").value(99));


        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(2);

    }


    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
    @WithUserDetails("alice")
    void givenValidPartners_whenAliceCallCreateGroupWithThem_thenGroupIsCreated() throws Exception {

        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("roomName", "Allisuh")
                .param("partnerId", "22222222-2222-2222-2222-222222222222") //bob
                .param("partnerId", "33333333-3333-3333-3333-333333333333"))//charlie
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.preference.customName").value("Allisuh"));

        assertThat(events.stream(RoomCreated.class)).hasSize(1);
        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(3);

    }


}
