package com.decade.practice.presence;

import com.decade.practice.BaseTestClass;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.dto.ChatResponse;
import com.decade.practice.presence.application.ports.in.PresenceSetter;
import com.decade.practice.web.security.UserClaims;
import com.decade.practice.web.security.jwt.JwtUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PresenceControllerTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private PresenceSetter presenceSetter;

      @Autowired
      private RedisTemplate<String, Object> redisTemplate;


      @Autowired
      private EngagementService engagementService;

      @Autowired
      private ObjectMapper objectMapper;


      @BeforeEach
      void setUp() {
            // Strict isolation: clear Redis before each test
            redisTemplate.getConnectionFactory().getConnection().flushAll();
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenAliceAndBobAreOnline_whenAliceListsOnline_thenReturnsBobOnly() throws Exception {
            // Given
            // Alice and Bob are online
            JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
            JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));

            presenceSetter.set(alice.getId(), alice.getClaims().name(), alice.getClaims().avatar(), Instant.now());
            presenceSetter.set(bob.getId(), bob.getClaims().name(), bob.getClaims().avatar(), Instant.now());

            // When & Then
            mockMvc.perform(get("/me/presences")
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(1))
                      .andExpect(jsonPath("$[0].username").value("bob"))
                      .andExpect(jsonPath("$[0].name").value("Bob Builder"));
      }

      @Test
      @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
      @WithUserDetails("alice")
      void givenOnlyAliceAndBobIsOnline_whenAliceCallGetsChatsPresence_thenOnlyBobChatIsOnline() throws Exception {
            // Given
            JwtUser alice = new JwtUser(new UserClaims(UUID.fromString("11111111-1111-1111-1111-111111111111"), "alice", "Alice Liddell", "vcl.jpg"));
            JwtUser bob = new JwtUser(new UserClaims(UUID.fromString("22222222-2222-2222-2222-222222222222"), "bob", "Bob Builder", "vcl.jpg"));
            JwtUser charlie = new JwtUser(new UserClaims(UUID.fromString("33333333-3333-3333-3333-333333333333"), "charlie", "Bob Builder", "vcl.jpg"));

            presenceSetter.set(bob.getId(), bob.getClaims().name(), bob.getClaims().avatar(), Instant.now());
            presenceSetter.set(alice.getId(), alice.getClaims().name(), alice.getClaims().avatar(), Instant.now());
            engagementService.getOrCreate(bob.getId(), alice.getId());
            engagementService.getOrCreate(charlie.getId(), alice.getId());
            // When & Then
            String bobChatId = alice.getId() + "+" + bob.getId();
            String charlieChatId = alice.getId() + "+" + charlie.getId();
            mockMvc.perform(get("/presences")
                                .queryParam("chatId", bobChatId)
                                .queryParam("chatId", charlieChatId)
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk()).andExpect(result -> {
                            String json = result.getResponse().getContentAsString();
                            Map<String, ChatResponse> map = objectMapper.readValue(json, Map.class);
                            assertNotNull(map.get(bobChatId));
                            assertTrue(map.get(bobChatId).lastActivity()
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

            presenceSetter.set(bob.getId(), bob.getClaims().name(), bob.getClaims().avatar(), Instant.now());
            presenceSetter.set(alice.getId(), alice.getClaims().name(), alice.getClaims().avatar(), Instant.now());
            presenceSetter.set(charlie.getId(), charlie.getClaims().name(), charlie.getClaims().avatar(), Instant.now());
            engagementService.getOrCreate(bob.getId(), alice.getId());
            engagementService.getOrCreate(charlie.getId(), alice.getId());
            // When & Then
            String bobChatId = alice.getId() + "+" + bob.getId();
            String charlieChatId = alice.getId() + "+" + charlie.getId();
            mockMvc.perform(get("/presences")
                                .queryParam("chatId", bobChatId)
                                .queryParam("chatId", charlieChatId)
                                .accept(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk()).andExpect(result -> {
                            String json = result.getResponse().getContentAsString();
                            Map<String, ChatResponse> map = objectMapper.readValue(json, Map.class);
                            assertNotNull(map.get(bobChatId));
                            assertTrue(map.get(bobChatId).lastActivity()
                                      .isAfter(Instant.now().minusSeconds(10)));

                            assertNotNull(map.get(charlieChatId));
                            assertTrue(map.get(charlieChatId).lastActivity()
                                      .isAfter(Instant.now().minusSeconds(10)));
                      });
      }

}
