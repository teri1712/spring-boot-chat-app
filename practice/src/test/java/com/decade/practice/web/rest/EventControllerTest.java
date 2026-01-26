package com.decade.practice.web.rest;

import com.decade.practice.common.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenValidTextEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
                {
                    "content": "Hello Bob"
                }
                """;

        // When & Then
        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatIdentifier)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.textEvent.content").value("Hello Bob"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenValidImageEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
                {
                    "downloadUrl": "http://example.com/image.jpg",
                    "width": 100,
                    "height": 100,
                    "filename": "vcl.jpg",
                    "format": "jpg"
                }
                """;

        // When & Then
        mockMvc.perform(post("/chats/{chatIdentifier}/image-events", chatIdentifier)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageEvent.downloadUrl").value("http://example.com/image.jpg"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenValidIconEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
                {
                    "resourceId": 5
                }
                """;
        // When & Then
        mockMvc.perform(post("/chats/{chatIdentifier}/icon-events", chatIdentifier)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.iconEvent.resourceId").value(5));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenValidSeenEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seenEvent.at").value("2016-01-24T10:15:30Z"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenValidFileEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
                {
                    "filename": "test.txt",
                    "size": 1024,
                    "mediaUrl": "http://example.com/test.txt"
                }
                """;
        // When & Then
        mockMvc.perform(post("/chats/{chatIdentifier}/file-events", chatIdentifier)
                        .header("Idempotency-key", UUID.randomUUID())

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileEvent.filename").value("test.txt"))
                .andExpect(jsonPath("$.fileEvent.size").value(1024));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenChatHasEvents_whenAliceListsEventsForChat_thenReturnsEventsOrderedByVersionDesc() throws Exception {
        // Given
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

        // Alice sends 2 messages to Bob to have some events
        for (int i = 1; i <= 2; i++) {
            String eventJson = """
                    {
                        "content": "Message %d"
                    }
                    """.formatted(i);
            mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatId)
                            .header("Idempotency-key", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(eventJson))
                    .andExpect(status().isCreated());
        }

        // When & Then
        // After 2 messages, Alice's eventVersion should be 2 (if it started at 0 and incVersion was called twice)
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].textEvent.content").value("Message 2"))
                .andExpect(jsonPath("$[1].textEvent.content").value("Message 1"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("alice")
    void givenUserHasEvents_whenAliceListsEventsForMe_thenReturnsAllHerEvents() throws Exception {
        // Given
        String bobChatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String charlieChatId = "11111111-1111-1111-1111-111111111111+33333333-3333-3333-3333-333333333333";

        // Alice sends message to Bob
        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", bobChatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"To Bob\" }"))
                .andExpect(status().isCreated());

        // Alice sends message to Charlie
        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", charlieChatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"To Charlie\" }"))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/users/me/events")
                        .param("atVersion", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
    @WithUserDetails("charlie")
    void givenCharlieSendsToAliceBobChat_whenCreateTextEvent_thenReturnsForbidden() throws Exception {
        // Given: Charlie is not part of Alice-Bob chat
        String chatIdentifier = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        String eventJson = """
                {
                    "content": "Intruding message"
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
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"})
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