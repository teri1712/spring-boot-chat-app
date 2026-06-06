package com.decade.practice.inbox.integration;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.integration.BaseTestClass;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BaseInboxTestClass extends BaseTestClass {


    UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

    String aliceBobChat = aliceId + "+" + bobId;
    String aliceAliceChat = aliceId + "+" + aliceId;
    String aliceCharlieChat = aliceId + "+" + charlieId;

    @MockitoSpyBean
    EngagementApi engagementApi;

    @Autowired
    ApplicationEvents events;

    @Autowired
    protected MockMvc mockMvc;

    @MockitoSpyBean
    LookUpRegistry lookUpRegistry;

    protected void sendText(String chatId, String content) throws Exception {
        Thread.sleep(1);
        mockMvc.perform(
                put("/chats/{id}/texts/{postingId}", chatId, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "content": "%s"
                        }
                        """.formatted(content))
            )
            .andExpect(status().isAccepted());
    }

    protected void sendImage(String chatId, String url) throws Exception {
        mockMvc.perform(
                put("/chats/{id}/images/{postingId}", chatId, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "file": {
                                "url": "%s",
                                "integrity": "integrity"
                            },
                            "filename": "image.jpg",
                            "width": 1000,
                            "height": 1000
                        }
                        """.formatted(url))
            )
            .andExpect(status().isAccepted());
    }

    protected void sendIcon(String chatId, int iconId) throws Exception {
        mockMvc.perform(
                put("/chats/{id}/icons/{postingId}", chatId, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "iconId": %d
                        }
                        """.formatted(iconId))
            )
            .andExpect(status().isAccepted());
    }

    protected void sendFile(String chatId, String filename) throws Exception {
        mockMvc.perform(
                put("/chats/{id}/files/{postingId}", chatId, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "filename": "%s",
                            "size": 1024,
                            "file": {
                                "url": "url",
                                "integrity": "integrity"
                            }
                        }
                        """.formatted(filename))
            )
            .andExpect(status().isAccepted());
    }

    protected void sendSeen(String chatId) throws Exception {
        mockMvc.perform(
                put("/chats/{id}/seens/{postingId}", chatId, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "at": "%s"
                        }
                        """.formatted(java.time.Instant.now().toString()))
            )
            .andExpect(status().isAccepted());
    }

    @BeforeEach
    void allowEngagement() {
        when(engagementApi.canRead(any(), any()))
            .thenReturn(true);

        when(engagementApi.canWrite(any(), any()))
            .thenReturn(true);
    }

    @BeforeEach
    void partnerSetup() {
        when(lookUpRegistry.registerLookUp(any()))
            .thenReturn(new PartnerLookUp() {
                @Override
                public Optional<Partner> lookUp(UUID id) {
                    if (id.equals(aliceId)) {
                        return Optional.of(new Partner(id, "Alice Liddell", "alice.jpg"));
                    }
                    if (id.equals(bobId)) {
                        return Optional.of(new Partner(id, "Bob Builder", "bob.jpg"));
                    }
                    return Optional.of(new Partner(id, "Charlie Brown", "charlie.jpg"));
                }
            });
    }

}
