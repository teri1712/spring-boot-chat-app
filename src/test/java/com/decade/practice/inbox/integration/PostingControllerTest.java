package com.decade.practice.inbox.integration;

import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.domain.events.*;
import com.decade.practice.resources.files.dto.PresignedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor
@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
class PostingControllerTest extends BaseInboxTestClass {

    final ObjectMapper objectMapper;
    final ConversationApi conversationApi;

    @Test
    void givenValidTextEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        // When & Then
        UUID idempotentKey = UUID.randomUUID();
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", aliceBobChat, idempotentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "content": "Hello Bob"
                        }
                        """
                ))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.postingId").value(idempotentKey.toString()));
        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(TextRoomEventCreated.class))
                    .hasSize(1);
                assertThat(events.stream(MessageCreated.class))
                    .hasSize(1);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(2);
            });


        // Idempotent check
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", aliceBobChat, idempotentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "content": "Hello Bob"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postingId").value(idempotentKey.toString()));

    }

    @Test
    void givenValidImageEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        MvcResult result = mockMvc.perform(post("/files/upload")
                .queryParam("filename", "teri.txt"))
            .andExpect(status().isOk())
            .andReturn();

        PresignedResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), PresignedResponse.class);

        String eTag = Assertions.assertDoesNotThrow(() -> {
            RestClient restClient = RestClient.builder()
                .build();


            return restClient.put()
                .uri(URI.create(response.getPresignedUploadUrl()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body("NAB Innovation Center Vietnam")
                .retrieve()
                .toBodilessEntity()
                .getHeaders().getETag();
        });


        mockMvc.perform(put("/chats/{chatIdentifier}/images/{postingId}", aliceBobChat, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "file" : {
                                    "eTag": "%s",
                                    "fileKey": "%s"
                                  },
                        "width": 200,
                        "height": 200,
                        "filename": "teri.txt",
                        "format": "jpg"
                    }
                    """.formatted(eTag.replace("\"", "\\\""), response.getFileKey())))
            .andExpect(status().isAccepted());


        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(ImageRoomEventCreated.class))
                    .hasSize(1);
                assertThat(events.stream(MessageCreated.class))
                    .hasSize(1);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(2);
            });

    }

    @Test
    void givenValidIconEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        // When & Then
        sendIcon(aliceBobChat, 5);
        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(IconRoomEventCreated.class))
                    .hasSize(1);
                assertThat(events.stream(MessageCreated.class))
                    .hasSize(1);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(2);
            });

    }

    @Test
    void givenNewMessageFromBob_whenAliceSeenToBobAgain_thenSeenPointerMoved() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        sendIcon(aliceBobChat, 5);
        sendSeen(aliceBobChat);

        // When
        sendIcon(aliceBobChat, 5);
        sendSeen(aliceBobChat);

        // Then
        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(SeenRoomEventCreated.class))
                    .hasSize(2);
                assertThat(events.stream(MessageCreated.class))
                    .hasSize(2);
                assertThat(events.stream(MessageUpdated.class))
                    .hasSize(3);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(10);
            });


        mockMvc.perform(get("/chats/{chatIdentifier}/messages", aliceBobChat)
                .queryParam("anchorSequenceNumber", "1000000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].seenBy.size()").value(1))
            .andExpect(jsonPath("$[0].seenBy[0].id").value(aliceId.toString()))
            .andExpect(jsonPath("$[1].seenBy.size()").value(0))

        ;

    }

    @Test
    void givenExistingMessageFromBob_whenAliceSeenToBob_thenMessageUpdadedWithSeenPointer() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        sendIcon(aliceBobChat, 5);

        // When & Then
        sendSeen(aliceBobChat);

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(SeenRoomEventCreated.class))
                    .hasSize(1);
                assertThat(events.stream(MessageUpdated.class))
                    .hasSize(1);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(4);
            });


        mockMvc.perform(get("/chats/{chatIdentifier}/messages", aliceBobChat)
                .queryParam("anchorSequenceNumber", "1000000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].seenBy.size()").value(1))
            .andExpect(jsonPath("$[0].seenBy[0].id").value(aliceId.toString()));


        mockMvc.perform(get("/conversations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identifier").value(aliceBobChat))
            .andExpect(jsonPath("$[0].recents.size()").value(1))
            .andExpect(jsonPath("$[0].recents[0].seenBy[0].id").value(aliceId.toString()))
        ;

    }

    @Test
    void givenValidFileEvent_whenAliceSendsToBob_thenStatusCreated() throws Exception {
        // Given
        conversationApi.create(aliceBobChat, aliceId, Set.of(aliceId, bobId), null);

        MvcResult result = mockMvc.perform(post("/files/upload")
                .queryParam("filename", "teri.txt"))
            .andExpect(status().isOk())
            .andReturn();

        PresignedResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), PresignedResponse.class);

        String eTag = Assertions.assertDoesNotThrow(() -> {
            RestClient restClient = RestClient.builder()
                .build();


            return restClient.put()
                .uri(URI.create(response.getPresignedUploadUrl()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body("NAB Innovation Center Vietnam")
                .retrieve()
                .toBodilessEntity()
                .getHeaders().getETag();
        });

        // When & Then
        mockMvc.perform(put("/chats/{chatIdentifier}/files/{postingId}", aliceBobChat, UUID.randomUUID())

                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "filename": "test.txt",
                        "size": 1024,
                        "file" : {
                                    "eTag": "%s",
                                    "fileKey": "%s"
                                  }
                    }
                    """.formatted(eTag.replace("\"", "\\\""), response.getFileKey())))
            .andExpect(status().isAccepted());

        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(events.stream(FileRoomEventCreated.class))
                    .hasSize(1);
                assertThat(events.stream(MessageCreated.class))
                    .hasSize(1);
                assertThat(events.stream(InboxLogCreated.class))
                    .hasSize(2);
            });

    }


    @Test
    void givenMalformedJson_whenCreateTextEvent_thenReturnsBadRequest() throws Exception {
        // Given
        String malformedJson = "{ \"textEvent\": { ... } }";

        // When & Then
        mockMvc.perform(put("/chats/{chatIdentifier}/texts/{postingId}", aliceBobChat, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());
    }
}
