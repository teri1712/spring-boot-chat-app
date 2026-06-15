package com.decade.practice.chat.integration;

import com.decade.practice.chatsettings.integration.SettingDataset;
import com.decade.practice.common.BaseTestClass;
import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.integration.EngagementDataset;
import com.decade.practice.inbox.integration.InboxDataset;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
@RequiredArgsConstructor
@ComponentTest(datasets = {InboxDataset.class, SettingDataset.class, EngagementDataset.class})
class ChatControllerTest extends BaseTestClass {

    @MockitoSpyBean
    EngagementApi engagementApi;

    @BeforeEach
    void allowEngagement() {
        when(engagementApi.canRead(any(), any()))
            .thenReturn(true);

        when(engagementApi.canWrite(any(), any()))
            .thenReturn(true);
    }

    final MockMvc mockMvc;


    @Test
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
    void givenAliceRequestChatWithNonExistentUser_whenGetChat_thenReturnsNotFoundError() throws Exception {
        // Given: Alice exists, but we request a chat with a random UUID
        String randomUser = UUID.randomUUID().toString();
        String chatId = "11111111-1111-1111-1111-111111111111+" + randomUser;

        // When & Then: getOrCreateChat will fail to find the second user
        mockMvc.perform(get("/chats/{id}", chatId))
            .andExpect(status().isNotFound());
    }


    @Test
    void givenUserNotPartOfChat_whenGetChat_thenReturnsForbidden() throws Exception {
        // Given: Alice is not part of Bob-Charlie chat

        String bobCharlieChat = "22222222-2222-2222-2222-222222222222+33333333-3333-3333-3333-333333333333";

        when(engagementApi.find(bobCharlieChat, UUID.fromString("11111111-1111-1111-1111-111111111111")))
            .thenThrow(new AccessDeniedException("User is not part of the chat"));

        // When & Then: Alice can't still request it
        mockMvc.perform(get("/chats/{id}", bobCharlieChat)
                .param("anchorSequenceNumber", "0"))
            .andExpect(status().isForbidden());
    }


    @Test
    void givenValidPartners_whenAliceCallCreateGroupWithThem_thenGroupIsCreated() throws Exception {

        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("roomName", "Allisuh")
                .param("partnerId", "22222222-2222-2222-2222-222222222222") //bob
                .param("partnerId", "33333333-3333-3333-3333-333333333333"))//charlie
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.preference.customName").value("Allisuh"));

    }


}
