package com.decade.practice.search.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.chat.application.ports.in.ChatService;
import com.decade.practice.search.application.ports.out.MessageDocumentRepository;
import com.decade.practice.search.application.ports.out.UserDocumentRepository;
import com.decade.practice.search.domain.UserDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SearchControllerTest extends BaseTestClass {

      @Autowired
      private MockMvc mockMvc;

      @Autowired
      private UserDocumentRepository userDocumentRepository;

      @Autowired
      private MessageDocumentRepository messageDocumentRepository;

      @Autowired
      private ChatService chatService;

      @Autowired
      private TestBeans.PrivateChatSender sender;

      @BeforeEach
      void setUp() {
            messageDocumentRepository.deleteAll();
            userDocumentRepository.deleteAll();
      }

      @Test
      @WithUserDetails("alice")
      void givenUsersExist_whenFindUsers_shouldReturnUserList() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDocument user = new UserDocument(userId, "searchable_user", "Searchable Name", "Male", "vcl.jpg");
            userDocumentRepository.save(user);

            mockMvc.perform(get("/users")
                                .param("query", "searchable")
                                .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$[0].name").value("Searchable Name"));
      }

      @Test
      @WithUserDetails("charlie")
      void givenUnParticipatedUser_whenFindMessages_thenReturnsUnauthorized() throws Exception {

            UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");

            chatService.getDirect(aliceId, bobId);

            mockMvc.perform(get("/me/history/messages")
                                .param("query", "hello")
                                .param("chatId", aliceId + "+" + bobId)
                                .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isForbidden());
      }

      @Test
      @WithUserDetails("alice")
      void givenAuthenticatedUser_whenFindMessages_thenReturnsMessageHistory() throws Exception {

            UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");

            chatService.getDirect(aliceId, bobId);
            sender.sendPrivateText("unique currentState content", bobId, aliceId);

            Assertions.assertEquals(1, messageDocumentRepository.count());
            Assertions.assertEquals("unique currentState content", messageDocumentRepository.findAll().iterator().next().getContent());


            mockMvc.perform(get("/me/history/messages")
                                .queryParam("query", "unique")
                                .queryParam("chatId", aliceId + "+" + bobId)
                                .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(1))
                      .andExpect(jsonPath("$[0].content").value("unique currentState content"));
      }

      @Test
      @WithUserDetails("alice")
      void givenEmptyQuery_whenFindUsers_thenReturnsEmptyList() throws Exception {
            mockMvc.perform(get("/users")
                                .param("query", "")
                                .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.length()").value(0));
      }
}
