package com.decade.practice.search.integration;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.search.application.ports.out.HistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.Person;
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
    private PeopleRepository peopleRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private TestBeans.PrivateChatSender sender;

    @BeforeEach
    void setUp() {
        historyRepository.deleteAll();
        peopleRepository.deleteAll();
    }

    @Test
    @WithUserDetails("alice")
    void givenUsersExist_whenFindUsers_shouldReturnUserList() throws Exception {
        UUID userId = UUID.randomUUID();
        Person user = new Person(null, userId, "searchable_user", "Searchable Name", "Male", "vcl.jpg");
        peopleRepository.save(user);

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

        mockMvc.perform(get("/chats/{chatId}/history", aliceId + "+" + bobId)

                .param("query", "hello").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("alice")
    void givenAuthenticatedUser_whenFindMessages_thenReturnsMessageHistory() throws Exception {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        chatService.getDirect(aliceId, bobId);
        sender.sendPrivateText("unique currentState content", bobId, aliceId);

        Assertions.assertEquals(1, historyRepository.count());
        Assertions.assertEquals("unique currentState content", historyRepository.findAll().iterator().next().content());


        mockMvc.perform(get("/chats/{chatId}/history", aliceId + "+" + bobId)
                .queryParam("query", "unique")
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
