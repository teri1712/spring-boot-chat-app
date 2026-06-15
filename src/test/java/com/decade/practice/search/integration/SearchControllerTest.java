package com.decade.practice.search.integration;

import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.inbox.domain.events.TextAdded;
import com.decade.practice.common.BaseTestClass;
import com.decade.practice.search.application.ports.out.HistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.MessageHistory;
import com.decade.practice.search.domain.Person;
import com.decade.practice.users.domain.events.UserCreated;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.Scenario;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class SearchControllerTest extends BaseTestClass {

    final MockMvc mockMvc;
    final PeopleRepository people;
    final HistoryRepository history;

    @MockitoSpyBean
    EngagementApi engagementApi;

    @Test
    @WithMockUser(username = "alice")
    void givenPersonExist_whenFindByThatPersonName_shouldReturnThePerson(Scenario scenario) throws Exception {
        UUID userId = UUID.randomUUID();
        scenario.publish(new UserCreated(userId, "alice", "Searchable Name", "male", Instant.now(), "vcl.jpg"))
            .andWaitForStateChange(new Supplier<Person>() {
                @Override
                public Person get() {
                    return people.findByUserId(userId).orElse(null);
                }
            }).andVerify(person -> {
                assertThat(person).extracting(Person::name)
                    .isEqualTo("Searchable Name");
                assertThat(person).extracting(Person::avatar)
                    .isEqualTo("vcl.jpg");
            });

        mockMvc.perform(get("/people")
                .param("query", "searchable")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Searchable Name"));
    }

    @Test
    @WithJwtUser(username = "charlie", id = "33333333-3333-3333-3333-333333333333")
    void givenUnParticipatedUser_whenFindMessages_thenReturnsUnauthorized() throws Exception {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        String chatId = aliceId + "+" + bobId;
        Mockito.when(engagementApi.canRead(chatId, aliceId))
            .thenReturn(false);

        mockMvc.perform(get("/chat-histories/{chatId}", chatId)
                .param("query", "hello").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithJwtUser(username = "alice")
    void givenAuthenticatedUser_whenFindMessages_thenReturnsMessageHistory(Scenario scenario) throws Exception {

        UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        String aliceBobChat = aliceId + "+" + bobId;

        Mockito.when(engagementApi.canRead(anyString(), any()))
            .thenReturn(true);
        scenario.publish(new TextAdded(1L, "unique currentState content", aliceBobChat, Instant.now(), UUID.randomUUID(), aliceId))
            .andWaitForStateChange(new Supplier<MessageHistory>() {
                @Override
                public MessageHistory get() {
                    return history.findBySequenceNumber(1L);
                }
            }).andVerify(message -> {
                assertThat(message).extracting(MessageHistory::content)
                    .isEqualTo("unique currentState content");
            });

        mockMvc.perform(get("/chat-histories/{chatId}", aliceBobChat)
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
