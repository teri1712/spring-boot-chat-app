package com.decade.practice.web.rest;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.data.repositories.EventRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.media.ImageStore;
import com.decade.practice.media.LocalMediaFileConfiguration;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.presence.UserPresenceService;
import com.decade.practice.security.SecurityConfiguration;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.security.strategy.LoginSuccessStrategy;
import com.decade.practice.security.strategy.LogoutStrategy;
import com.decade.practice.security.strategy.Oauth2LoginSuccessStrategy;
import com.decade.practice.usecases.ChatEventStore;
import com.decade.practice.usecases.ChatOperations;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.decade.practice.utils.Media.ONE_PIXEL_BMP_BYTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@Import({LocalMediaFileConfiguration.class, SecurityConfiguration.class})
class EventControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ChatEventStore chatEventStore;

        @MockBean
        private SimpMessagingTemplate template;

        @MockBean
        private ChatOperations chatOperations;

        @MockBean
        private EventRepository eventRepository;

        @MockBean
        private JwtCredentialService jwtCredentialService;

        @MockBean
        private UserPresenceService userPresenceService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private ImageStore imageStore;

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

        @BeforeEach
        void setUp() {
                testUser = new User("testuser", " password");
                when(userRepository.getByUsername("testuser")).thenReturn(testUser);
        }

        @Test
        @WithMockUser("testuser")
        void createEvent_shouldReturnCreated_whenTextEventIsSent() throws Exception {
                TextEvent event = new TextEvent(new Chat(testUser, testUser), testUser, "test message");

                when(chatEventStore.save(any(ChatEvent.class))).thenReturn(Collections.singleton(event));

                event.setLocalId(UUID.randomUUID());

                mockMvc.perform(post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(event)))
                        .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser("testuser")
        void createEvent_shouldReturnCreated_whenImageEventIsSent() throws Exception {
                ImageEvent event = new ImageEvent(new Chat(testUser, testUser), testUser, new ImageSpec());
                event.setLocalId(UUID.randomUUID());
                when(chatEventStore.save(any(ImageEvent.class))).thenReturn(Collections.singleton(event));

                MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json", objectMapper.writeValueAsString(event).getBytes());
                MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "image/bmp", ONE_PIXEL_BMP_BYTES);

                mockMvc.perform(multipart("/events")
                                .file(eventPart)
                                .file(filePart))
                        .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser("testuser")
        void listEvents_forChat_shouldReturnOk() throws Exception {
                Chat chat = new Chat(testUser, testUser);

                when(chatOperations.getOrCreateChat(any(ChatIdentifier.class))).thenReturn(chat);
                when(eventRepository.findByOwnerAndChatAndEventVersionLessThanEqual(any(), any(), anyInt(), any()))
                        .thenReturn(List.of());
                String chatId = chat.getIdentifier().toString();
                mockMvc.perform(get("/chats/{chatId}/events", chatId)
                                .param("atVersion", "0"))
                        .andExpect(status().isOk());
        }

        @Test
        @WithMockUser("testuser")
        void listEvents_forUser_shouldReturnOk() throws Exception {
                when(eventRepository.findByOwnerAndEventVersionLessThanEqual(any(), anyInt(), any()))
                        .thenReturn(List.of());

                mockMvc.perform(get("/users/me/events")
                                .param("atVersion", "0"))
                        .andExpect(status().isOk());
        }
}