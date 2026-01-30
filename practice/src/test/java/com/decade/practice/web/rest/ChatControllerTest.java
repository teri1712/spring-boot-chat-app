package com.decade.practice.web.rest;

import com.decade.practice.api.dto.PreferenceResponse;
import com.decade.practice.common.BaseTestClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ChatControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenAliceHasChats_whenAliceListsChats_thenReturnsAllAliceChats() throws Exception {
        // Given: Alice has 2 chats (with Bob and Charlie) from seed_chats.sql

        // When & Then
        mockMvc.perform(get("/chats")
                        .param("atVersion", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].conversation.partner.username").value("bob"))
                .andExpect(jsonPath("$[1].conversation.partner.username").value("charlie"))
        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("bob")
    void givenBobHasOneChat_whenBobListsChats_thenReturnsOnlyBobChat() throws Exception {
        // Given: Bob has 1 chat (with Alice) from seed_chats.sql

        // When & Then
        mockMvc.perform(get("/chats")
                        .param("atVersion", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].conversation.partner.username").value("alice"))

        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
        // Given
        // Correct order: 1111... < 2222...
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        PreferenceResponse preference = new PreferenceResponse();
        preference.setRoomName("My pookie bob");
        preference.setIconId(99);

        // When
        mockMvc.perform(put("/chats/{id}/preference", chatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preference)))
                .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/chats/{id}", chatId)
                        .param("atVersion", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preference.roomName").value("My pookie bob"))
                .andExpect(jsonPath("$.preference.iconId").value(99));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenAliceHasMultipleChats_whenAliceSendsMessageToOlderChat_thenThatChatMovesToTop() throws Exception {
        // Given
        // Alice has chats with Bob (ID: ...2222) and Charlie (ID: ...3333)
        // From seed_chats.sql:
        // Bob chat is created first (current_version 0)
        // Charlie chat is created second (current_version 0)
        String charlieChatId = "11111111-1111-1111-1111-111111111111+33333333-3333-3333-3333-333333333333";

        // When: Alice sends a message to Charlie
        String eventJson = """
                {
                    "content": "New message to Charlie"
                }
                """;

        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", charlieChatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated());

        // Then: Charlie's chat should be the first in the list
        // Alice's event version should now be 1
        mockMvc.perform(get("/chats")
                        .param("atVersion", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].conversation.partner.username").value("charlie"))
                .andExpect(jsonPath("$[1].conversation.partner.username").value("bob"))
        ;
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenUserListsChatsWithWrongVersion_whenListChats_thenReturnsBadRequestError() throws Exception {
        // Given: Alice's current version is 0

        // When & Then: Request with version 1 should fail
        mockMvc.perform(get("/chats")
                        .param("atVersion", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
    @WithUserDetails("alice")
    void givenThemesExist_whenRequestThemes_thenReturnsAllThemes() throws Exception {
        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }


    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceRequestChatWithNonExistentUser_whenGetChat_thenReturnsNotFoundError() throws Exception {
        // Given: Alice exists, but we request a chat with a random UUID
        String randomUser = UUID.randomUUID().toString();
        String chatId = "11111111-1111-1111-1111-111111111111+" + randomUser;

        // When & Then: getOrCreateChat will fail to find the second user
        mockMvc.perform(get("/chats/{id}", chatId)
                        .param("atVersion", "0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenUserNotPartOfChat_whenGetChat_thenReturnsForbidden() throws Exception {
        // Given: Alice is not part of Bob-Charlie chat
        // Wait, seed_chats only has Alice-Bob and Alice-Charlie.
        // Let's assume Bob-Charlie chat is created.
        String bobCharlieChat = "22222222-2222-2222-2222-222222222222+33333333-3333-3333-3333-333333333333";

        // When & Then: Alice can still request it (current implementation has no check)
        mockMvc.perform(get("/chats/{id}", bobCharlieChat)
                        .param("atVersion", "0"))
                .andExpect(status().isForbidden());
    }
}
