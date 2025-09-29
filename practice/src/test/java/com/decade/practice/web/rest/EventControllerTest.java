package com.decade.practice.web.rest;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.adapter.security.jwt.JwtService;
import com.decade.practice.adapter.security.strategies.LoginSuccessStrategy;
import com.decade.practice.adapter.security.strategies.LogoutStrategy;
import com.decade.practice.adapter.security.strategies.Oauth2LoginSuccessStrategy;
import com.decade.practice.adapter.web.rest.EventController;
import com.decade.practice.application.usecases.*;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.infra.configs.SecurityConfiguration;
import com.decade.practice.utils.PrerequisiteBeans;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.decade.practice.utils.Media.ONE_PIXEL_BMP_BYTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@Import({SecurityConfiguration.class})
class EventControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DeliveryService deliveryService;

        @MockBean
        private ChatService chatService;

        @MockBean
        private EventService eventService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private UserPresenceService userPresenceService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private ImageStore imageStore;

        @MockBean
        private MediaStore mediaStore;

        @MockBean
        private LoginSuccessStrategy loginSuccessStrategy;

        @MockBean
        private Oauth2LoginSuccessStrategy oauth2LoginSuccessStrategy;

        @MockBean
        private LogoutStrategy logoutStrategy;

        @MockBean
        private UserService userService;

        @MockBean
        private ConversationRepository conversationRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private User testUser;

        @BeforeEach
        void setUp() {
                testUser = new User("testuser", " password");
                when(userRepository.findByUsername("testuser")).thenReturn(testUser);
                when(deliveryService.createAndSend(any(User.class), any(ChatEvent.class)))
                        .thenAnswer(inv -> inv.getArgument(1));
        }

        @Test
        @WithMockUser("testuser")
        void createEvent_shouldReturnCreated_whenTextEventIsSent() throws Exception {
                Chat chat = new Chat(testUser, testUser);

                when(conversationRepository.getUser(any(String.class))).thenReturn(testUser);
                when(chatService.getOrCreateChat(any(ChatIdentifier.class))).thenReturn(chat);
                TextEvent event = new TextEvent(new Chat(testUser, testUser), testUser, "test message");


                when(deliveryService.createAndSend(any(User.class), any(ChatEvent.class)))
                        .thenReturn(event);

                mockMvc.perform(post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(event)))
                        .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser("testuser")
        void createEvent_shouldReturnCreated_whenImageEventIsSent() throws Exception {
                Chat chat = new Chat(testUser, testUser);

                when(conversationRepository.getUser(any(String.class))).thenReturn(testUser);
                when(chatService.getOrCreateChat(any(ChatIdentifier.class))).thenReturn(chat);
                ImageEvent event = new ImageEvent(new Chat(testUser, testUser), testUser, new ImageSpec());

                when(deliveryService.createAndSend(any(User.class), any(ChatEvent.class)))
                        .thenReturn(event);

                when(imageStore.save(any(BufferedImage.class)))
                        .thenReturn(new ImageSpec("uri:", "file.jpg", 1, 1, "jpg"));

                MockMultipartFile eventPart = new MockMultipartFile("event", "", "application/json", objectMapper.writeValueAsString(event).getBytes());
                MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "image/bmp", ONE_PIXEL_BMP_BYTES);

                mockMvc.perform(multipart("/events")
                                .file(eventPart)
                                .file(filePart).header("X-file-type", "image"))
                        .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser("testuser")
        void listEvents_forChat_shouldReturnOk() throws Exception {
                Chat chat = new Chat(testUser, testUser);
                when(conversationRepository.getUser(any(String.class))).thenReturn(testUser);

                when(chatService.getOrCreateChat(any(ChatIdentifier.class))).thenReturn(chat);
                when(eventService.findByOwnerAndChatAndEventVersionLessThanEqual(any(), any(), anyInt()))
                        .thenReturn(List.of());
                String chatId = chat.getIdentifier().toString();
                mockMvc.perform(get("/chats/{chatId}/events", chatId)
                                .param("atVersion", "0"))
                        .andExpect(status().isOk());
        }

        @Test
        @WithMockUser("testuser")
        void listEvents_forUser_shouldReturnOk() throws Exception {
                when(eventService.findByOwnerAndEventVersionLessThanEqual(any(), anyInt()))
                        .thenReturn(List.of());

                mockMvc.perform(get("/users/me/events")
                                .param("atVersion", "0"))
                        .andExpect(status().isOk());
        }
}