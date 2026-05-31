package com.decade.practice.search.integration;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.search.application.ports.out.MessageHistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.MessageHistory;
import com.decade.practice.search.domain.Person;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends BaseTestClass {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PeopleRepository people;

    @Autowired
    MessageHistoryRepository history;

    @MockitoSpyBean
    EngagementApi engagementApi;

    @Test
    @WithMockUser(username = "alice")
    void givenPersonExist_whenFindByThatPersonName_shouldReturnThePerson() throws Exception {
        UUID userId = UUID.randomUUID();
        Person person = new Person(null, userId, "searchable_user", "Searchable Name", "Male", "vcl.jpg");
        people.save(person);

        mockMvc.perform(get("/people")
                .param("query", "searchable")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Searchable Name"));
    }

    @Test
    void givenUnParticipatedUser_whenFindMessages_thenReturnsUnauthorized() throws Exception {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        String chatId = aliceId + "+" + bobId;
        Mockito.when(engagementApi.canRead(chatId, aliceId))
            .thenReturn(false);

        mockMvc.perform(get("/chat-histories/{chatId}", chatId)
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("id", aliceId)
                    ))
                .param("query", "hello").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenFindMessages_thenReturnsMessageHistory() throws Exception {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        String aliceBobChat = aliceId + "+" + bobId;

        Mockito.when(engagementApi.canRead(anyString(), any()))
            .thenReturn(true);
        MessageHistory message = new MessageHistory(null, "unique currentState content", 1L, aliceBobChat, Instant.now());
        history.save(message);

        mockMvc.perform(get("/chat-histories/{chatId}", aliceBobChat)
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("id", aliceId)
                    ))
                .queryParam("query", "unique")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].content").value("unique currentState content"));
    }

    @Test
    @WithMockUser(username = "alice")
    void givenEmptyQuery_whenFindUsers_thenReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/people")
                .param("query", "")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
