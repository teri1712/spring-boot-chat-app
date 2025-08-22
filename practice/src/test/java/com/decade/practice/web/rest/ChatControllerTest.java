package com.decade.practice.web.rest;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.data.repositories.ChatRepository;
import com.decade.practice.data.repositories.ThemeRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.ChatSnapshot;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.embeddable.Preference;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.security.SecurityConfiguration;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.security.strategy.LoginSuccessStrategy;
import com.decade.practice.security.strategy.LogoutStrategy;
import com.decade.practice.security.strategy.Oauth2LoginSuccessStrategy;
import com.decade.practice.usecases.ChatOperations;
import com.decade.practice.usecases.EventOperations;
import com.decade.practice.usecases.UserOperations;
import com.decade.practice.utils.PrerequisiteBeans;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@Import({SecurityConfiguration.class})
class ChatControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private ChatRepository chatRepository;

        @MockBean
        private ChatOperations chatOperations;

        @MockBean
        private ThemeRepository themeRepository;

        @MockBean
        private EventOperations eventOperations;

        // Security-related mocks to satisfy the SecurityConfiguration
        @MockBean
        private JwtCredentialService jwtCredentialService;

        @MockBean
        private LoginSuccessStrategy loginSuccessStrategy;

        @MockBean
        private Oauth2LoginSuccessStrategy oauth2LoginSuccessStrategy;

        @MockBean
        private LogoutStrategy logoutStrategy;

        @MockBean
        private UserOperations userOperations;

        @Autowired
        private ObjectMapper objectMapper;

        private User testUser;
        private Chat chatEntity;
        private ChatIdentifier identifier;

        @BeforeEach
        void setUp() {
                testUser = new User("alice", "pwd");
                testUser.setSyncContext(new SyncContext(testUser));
                testUser.getSyncContext().setEventVersion(43);
                given(userRepository.getByUsername("alice")).willReturn(testUser);
                given(eventOperations.createAndSend(any(User.class), any(PreferenceEvent.class)))
                        .willAnswer(inv -> inv.getArgument(1));

                // Prepare a chat and its identifier
                User u1 = new User("u1", "p1");
                User u2 = new User("u2", "p2");
                chatEntity = new Chat(u1, u2);
                identifier = chatEntity.getIdentifier();
        }

        @Test
        @WithMockUser("alice")
        void get_chat_shouldReturnOk_andUseDefaultAtVersion() throws Exception {
                ChatSnapshot snapshot = new ChatSnapshot(null, null, 43);
                given(chatOperations.getOrCreateChat(any(ChatIdentifier.class))).willReturn(chatEntity);
                given(chatOperations.getSnapshot(eq(chatEntity), eq(testUser), eq(43))).willReturn(snapshot);

                mockMvc.perform(get("/chats/{id}", identifier.toString()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.atVersion").value(43));
        }

        @Test
        @WithMockUser("alice")
        void get_chat_withExplicitVersion_shouldReturnOk() throws Exception {
                ChatSnapshot snapshot = new ChatSnapshot(null, null, 7);
                given(chatOperations.getOrCreateChat(any(ChatIdentifier.class))).willReturn(chatEntity);
                given(chatOperations.getSnapshot(eq(chatEntity), eq(testUser), eq(7))).willReturn(snapshot);

                mockMvc.perform(get("/chats/{id}", identifier.toString()).param("atVersion", "7"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.atVersion").value(7));
        }

        @Test
        @WithMockUser("alice")
        void patch_preference_shouldPersistAndReturnLocalChat() throws Exception {
                given(chatOperations.getOrCreateChat(any(ChatIdentifier.class))).willReturn(chatEntity);
                Theme theme = new Theme(5, null);
                given(themeRepository.findById(5)).willReturn(Optional.of(theme));

                Preference preference = new Preference();
                preference.setResourceId(10);
                preference.setRoomName("Room A");
                Theme prefTheme = new Theme(5, null);
                preference.setTheme(prefTheme);

                mockMvc.perform(patch("/chats/{id}/preference", identifier.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(preference)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.resourceId").value(10))
                        .andExpect(jsonPath("$.roomName").value("Room A"))
                        .andExpect(jsonPath("$.theme.id").value(5));
        }

        @Test
        @WithMockUser("alice")
        void get_themes_shouldReturnOk_andPublicCache() throws Exception {
                given(themeRepository.findAll()).willReturn(List.of(new Theme(1, null), new Theme(2, null)));

                mockMvc.perform(get("/chats/themes"))
                        .andExpect(status().isOk())
                        .andExpect(header().string(org.springframework.http.HttpHeaders.CACHE_CONTROL, org.hamcrest.Matchers.containsString("public")))
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[1].id").value(2));
        }

        @Test
        @WithMockUser("alice")
        void list_chats_shouldReturnOk_andSnapshots() throws Exception {
                // startAt optional; still pass it to exercise converter
                given(chatRepository.findById(any(ChatIdentifier.class))).willReturn(Optional.of(chatEntity));

                Chat c1 = chatEntity;
                Chat c2 = new Chat(new User("a", "p"), new User("b", "p"));
                given(chatOperations.listChat(eq(testUser), anyInt(), any())).willReturn(List.of(c1, c2));

                ChatSnapshot s1 = new ChatSnapshot(null, null, 100);
                ChatSnapshot s2 = new ChatSnapshot(null, null, 100);
                given(chatOperations.getSnapshot(eq(c1), eq(testUser), eq(100))).willReturn(s1);
                given(chatOperations.getSnapshot(eq(c2), eq(testUser), eq(100))).willReturn(s2);

                mockMvc.perform(get("/chats")
                                .param("atVersion", "100")
                                .param("startAt", identifier.toString()))
                        .andExpect(status().isOk())
                        .andExpect(header().string(org.springframework.http.HttpHeaders.CACHE_CONTROL, org.hamcrest.Matchers.containsString("max-age")))
                        .andExpect(jsonPath("$[0].atVersion").value(100))
                        .andExpect(jsonPath("$[1].atVersion").value(100));
        }
}
