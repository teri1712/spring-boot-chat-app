package com.decade.practice.inbox.integration;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.inbox.domain.events.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PostingControllerTest extends BaseTestClass {

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
    void givenValidTextEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        // Create chat first
        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
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
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", chatIdentifier, idempotentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.postingId").value(idempotentKey));


        assertThat(events.stream(TextRoomEventCreated.class)).hasSize(1);

        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(2);


        // Idempotent check
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", chatIdentifier, idempotentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postingId").value(idempotentKey));

    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenValidImageEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        // Create chat first
        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
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
        mockMvc.perform(put("/chats/{chatIdentifier}/images/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());

        assertThat(events.stream(ImageRoomEventCreated.class)).hasSize(1);

        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(2);
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenValidIconEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
            {
                "iconId": 5
            }
            """;
        // When & Then
        mockMvc.perform(put("/chats/{chatIdentifier}/icons/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());


        assertThat(events.stream(IconRoomEventCreated.class)).hasSize(1);
        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(2);
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenNewMessageFromBob_whenAliceSeenToBobAgain_thenSeenPointerMoved() throws Exception {
        // Given

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        String alice = "11111111-1111-1111-1111-111111111111";
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String iconJson = """
            {
                "iconId": 5
            }
            """;
        mockMvc.perform(put("/chats/{chatIdentifier}/icons/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(iconJson))
            .andExpect(status().isAccepted());

        String eventJson = """
            {
                "at": "2016-01-24T10:15:30Z"
            }
            """;

        mockMvc.perform(put("/chats/{chatIdentifier}/seens/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());
        // When

        mockMvc.perform(put("/chats/{chatIdentifier}/icons/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(iconJson))
            .andExpect(status().isAccepted());

        mockMvc.perform(put("/chats/{chatIdentifier}/seens/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());

        // Then
        assertThat(events.stream(SeenRoomEventCreated.class)).hasSize(2);
        assertThat(events.stream(MessageCreated.class)).hasSize(2);
        assertThat(events.stream(MessageUpdated.class)).hasSize(3);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(10);

        mockMvc.perform(get("/chats/{chatIdentifier}/messages", chatIdentifier, UUID.randomUUID())
                .queryParam("anchorSequenceNumber", "1000000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].seenBy.size()").value(1))
            .andExpect(jsonPath("$[0].seenBy[0].id").value(alice))
            .andExpect(jsonPath("$[1].seenBy.size()").value(0))

        ;

    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenExistingMessageFromBob_whenAliceSeenToBob_thenMessageUpdadedWithSeenPointer() throws Exception {
        // Given

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
            )
            .andExpect(status().isCreated());

        String alice = "11111111-1111-1111-1111-111111111111";
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String iconJson = """
            {
                "iconId": 5
            }
            """;
        mockMvc.perform(put("/chats/{chatIdentifier}/icons/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(iconJson))
            .andExpect(status().isAccepted());

        // When & Then
        String eventJson = """
            {
                "at": "2016-01-24T10:15:30Z"
            }
            """;

        mockMvc.perform(put("/chats/{chatIdentifier}/seens/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());


        assertThat(events.stream(SeenRoomEventCreated.class)).hasSize(1);
        assertThat(events.stream(MessageUpdated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(4);

        mockMvc.perform(get("/chats/{chatIdentifier}/messages", chatIdentifier, UUID.randomUUID())
                .queryParam("anchorSequenceNumber", "1000000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].seenBy.size()").value(1))
            .andExpect(jsonPath("$[0].seenBy[0].id").value(alice));


        mockMvc.perform(get("/conversations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identifier").value(chatIdentifier))
            .andExpect(jsonPath("$[0].recents.size()").value(1))
            .andExpect(jsonPath("$[0].recents[0].seenBy[0].id").value(alice))
        ;

    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenValidFileEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given

        mockMvc.perform(put("/direct-chats/{partnerId}", "22222222-2222-2222-2222-222222222222")
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
        mockMvc.perform(put("/chats/{chatIdentifier}/files/{postingId}", chatIdentifier, UUID.randomUUID())

                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
            .andExpect(status().isAccepted());


        assertThat(events.stream(FileRoomEventCreated.class)).hasSize(1);

        assertThat(events.stream(MessageCreated.class)).hasSize(1);
        assertThat(events.stream(InboxLogCreated.class)).hasSize(2);
    }


    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenMalformedJson_whenCreateTextEvent_thenReturnsBadRequest() throws Exception {
        // Given
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String malformedJson = "{ \"textEvent\": { ... } }";

        // When & Then
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", chatIdentifier, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());
    }
}
