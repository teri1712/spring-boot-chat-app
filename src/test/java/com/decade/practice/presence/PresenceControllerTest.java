package com.decade.practice.presence;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.engagement.domain.events.StalkEvent;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.integration.TestBeans;
import com.decade.practice.presence.application.ports.out.PresenceRepository;
import com.decade.practice.presence.domain.Presence;
import com.decade.practice.presence.dto.RoomPresenceResponse;
import com.decade.practice.shared.security.UserClaims;
import com.decade.practice.shared.security.jwt.JwtUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PresenceControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ApplicationEvents events;
    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestBeans.PrivateChatSender chatSender;

    @Autowired
    private PresenceRepository presences;


    @BeforeEach
    @AfterEach
    void clean() {
        // Strict isolation: clear Redis before each test
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceStalkBob_whenAliceListsBuddies_thenReturnsBobOnly() throws Exception {
        // Given
        // Alice and Bob are online
        JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
        JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));

        presenceRepository.save(new Presence(alice.getId(), Instant.now()));
        presenceRepository.save(new Presence(bob.getId(), Instant.now()));


        chatService.getDirect(alice.getId(), bob.getId());

        assertThat(events.stream(StalkEvent.class)).hasSize(1);

        // When & Then
        mockMvc.perform(get("/buddy-presences")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].userId").value(bob.getId().toString()))
            .andExpect(jsonPath("$[0].name").value("Bob Builder"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceStalkCharlieMoreThanBob_whenAliceListsBuddies_thenReturnsCharlieFirst() throws Exception {
        // Given
        // Alice and Bob are online
        JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
        JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));
        JwtUser charlie = new JwtUser(new UserClaims(UUID.fromString("33333333-3333-3333-3333-333333333333"), "charlie", "Charlie Brown", "vcl.jpg"));

        presenceRepository.save(new Presence(alice.getId(), Instant.now()));
        presenceRepository.save(new Presence(bob.getId(), Instant.now()));
        presenceRepository.save(new Presence(charlie.getId(), Instant.now()));

        chatService.getDirect(alice.getId(), bob.getId());
        chatService.getDirect(alice.getId(), charlie.getId());
        chatService.getDirect(alice.getId(), charlie.getId());
        assertThat(events.stream(StalkEvent.class)).hasSize(3);

        // When & Then
        mockMvc.perform(get("/buddy-presences")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value(charlie.getId().toString()))
            .andExpect(jsonPath("$[0].name").value("Charlie Brown"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenOnlyAliceAndBobIsOnline_whenAliceCallGetsChatsPresence_thenOnlyBobChatIsOnline() throws Exception {
        // Given
        JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
        JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));
        JwtUser charlie = new JwtUser(new UserClaims(UUID.fromString("33333333-3333-3333-3333-333333333333"), "charlie", "Charlie", "vcl.jpg"));

        presenceRepository.save(new Presence(bob.getId(), Instant.now()));
        presenceRepository.save(new Presence(alice.getId(), Instant.now()));
        assertEquals(2, presences.count());
        chatService.getDirect(bob.getId(), alice.getId());
        chatService.getDirect(charlie.getId(), alice.getId());
        assertEquals(2, presences.count());

        // When & Then
        String bobChatId = alice.getId() + "+" + bob.getId();
        String charlieChatId = alice.getId() + "+" + charlie.getId();
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

                // bc charlie doesn't onlien
                assertNull(map.get(charlieChatId));
            });
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAllAreOnline_whenAliceCallGetsChatsPresence_thenReturnsAllRequestedChatAreOnline() throws Exception {
        // Given
        JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
        JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));
        JwtUser charlie = new JwtUser(new UserClaims(UUID.fromString("33333333-3333-3333-3333-333333333333"), "charlie", "Bob Builder", "vcl.jpg"));

        presenceRepository.save(new Presence(bob.getId(), Instant.now()));
        presenceRepository.save(new Presence(alice.getId(), Instant.now()));
        presenceRepository.save(new Presence(charlie.getId(), Instant.now()));
        chatService.getDirect(bob.getId(), alice.getId());
        chatService.getDirect(charlie.getId(), alice.getId());
        chatSender.emitText("meomeo", bob.getId(), alice.getId());
        // When & Then
        String bobChatId = alice.getId() + "+" + bob.getId();
        String charlieChatId = alice.getId() + "+" + charlie.getId();
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
