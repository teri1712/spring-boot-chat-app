package com.decade.practice.presence;

import com.decade.practice.engagement.domain.events.StalkEvent;
import com.decade.practice.inbox.domain.events.RoomCreated;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.presence.dto.RoomPresenceResponse;
import com.decade.practice.shared.security.jwt.WithJwtUser;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.decade.practice.web.events.ConnectionInteracted;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor
public class PresenceControllerTest extends BaseTestClass {

    final MockMvc mockMvc;
    final ObjectMapper objectMapper;
    final ApplicationEventPublisher publisher;

    @MockitoSpyBean
    UserApi userApi;

    @BeforeEach
    void setUp() {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        when(userApi.getUserInfo(any()))
            .thenReturn(Map.of(
                aliceId, new UserInfo("alice", "alice", "alice", aliceId),
                bobId, new UserInfo("Bob Builder", "Bob Builder", "bob", bobId),
                charlieId, new UserInfo("Charlie Brown", "Charlie Brown", "charlie", charlieId)
            ));
    }


    @Test
    @WithJwtUser
    void givenAliceStalkBob_whenAliceListsBuddies_thenReturnsBobOnly() throws Exception {
        // Given
        // Alice and Bob are online
        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        publisher.publishEvent(new ConnectionInteracted(aliceId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(bobId, "localhost", Instant.now(), "Chrome"));

        publisher.publishEvent(new StalkEvent(aliceId, bobId));

        // When & Then
        mockMvc.perform(get("/buddy-presences")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].userId").value(bobId.toString()))
            .andExpect(jsonPath("$[0].name").value("Bob Builder"));
    }

    @Test
    @WithJwtUser
    void givenAliceStalkCharlieMoreThanBob_whenAliceListsBuddies_thenReturnsCharlieFirst() throws Exception {
        // Given
        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        publisher.publishEvent(new ConnectionInteracted(aliceId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(bobId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(charlieId, "localhost", Instant.now(), "Chrome"));

        publisher.publishEvent(new StalkEvent(aliceId, bobId));
        publisher.publishEvent(new StalkEvent(aliceId, charlieId));
        publisher.publishEvent(new StalkEvent(aliceId, charlieId));

        // When & Then
        mockMvc.perform(get("/buddy-presences")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value(charlieId.toString()))
            .andExpect(jsonPath("$[0].name").value("Charlie Brown"));
    }

    @Test
    @WithJwtUser
    void givenOnlyAliceAndBobIsOnline_whenAliceCallGetsChatsPresence_thenOnlyBobChatIsOnline(Scenario scenario) throws Exception {
        // Given

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");


        publisher.publishEvent(new ConnectionInteracted(aliceId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(bobId, "localhost", Instant.now(), "Chrome"));

        // When & Then
        String bobChatId = aliceId + "+" + bobId;
        String charlieChatId = aliceId + "+" + charlieId;
        scenario.publish(new RoomCreated(bobChatId, aliceId, Instant.now(), Set.of(aliceId, bobId)))
            .andWaitForStateChange(() -> 1L);

        scenario.publish(new RoomCreated(charlieChatId, aliceId, Instant.now(), Set.of(aliceId, charlieId)))
            .andWaitForStateChange(() -> 1L);
        mockMvc.perform(get("/presences")
                .queryParam("chatId", bobChatId)
                .queryParam("chatId", charlieChatId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(result -> {
                String json = result.getResponse().getContentAsString();
                Map<String, RoomPresenceResponse> map = objectMapper.readValue(json, new TypeReference<Map<String, RoomPresenceResponse>>() {
                });
                assertNotNull(map.get(bobChatId));
                assertTrue(map.get(bobChatId).at()
                    .isAfter(Instant.now().minusSeconds(10)));

                // bc charlie doesn't online
                assertNull(map.get(charlieChatId));
            });
    }

    @Test
    @WithJwtUser
    void givenAllAreOnline_whenAliceCallGetsChatsPresence_thenReturnsAllRequestedChatAreOnline(Scenario scenario) throws Exception {
        // Given
        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlieId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        publisher.publishEvent(new ConnectionInteracted(aliceId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(bobId, "localhost", Instant.now(), "Chrome"));
        publisher.publishEvent(new ConnectionInteracted(charlieId, "localhost", Instant.now(), "Chrome"));

        // When & Then
        String bobChatId = aliceId + "+" + bobId;
        String charlieChatId = aliceId + "+" + charlieId;


        scenario.publish(new RoomCreated(bobChatId, aliceId, Instant.now(), Set.of(aliceId, bobId)))
            .andWaitForStateChange(() -> 1L);

        scenario.publish(new RoomCreated(charlieChatId, aliceId, Instant.now(), Set.of(aliceId, charlieId)))
            .andWaitForStateChange(() -> 1L);
        mockMvc.perform(get("/presences")
                .queryParam("chatId", bobChatId)
                .queryParam("chatId", charlieChatId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(result -> {
                String json = result.getResponse().getContentAsString();
                Map<String, RoomPresenceResponse> map = objectMapper.readValue(json, new TypeReference<Map<String, RoomPresenceResponse>>() {
                });
                assertNotNull(map.get(bobChatId));
                assertTrue(map.get(bobChatId).at()
                    .isAfter(Instant.now().minusSeconds(10)));

                assertNotNull(map.get(charlieChatId));
                assertTrue(map.get(charlieChatId).at()
                    .isAfter(Instant.now().minusSeconds(10)));
            });
    }

}
