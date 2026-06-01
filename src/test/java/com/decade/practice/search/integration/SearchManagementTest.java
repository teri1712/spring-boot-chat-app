package com.decade.practice.search.integration;


import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.inbox.domain.events.TextAdded;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.integration.TestBeans;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.application.queries.SearchService;
import com.decade.practice.search.domain.Person;
import com.decade.practice.search.dto.MessageResponse;
import com.decade.practice.search.dto.PeopleResponse;
import com.decade.practice.users.domain.events.ProfileChanged;
import com.decade.practice.users.domain.events.UserCreated;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SearchManagementTest extends BaseTestClass {

    @MockitoSpyBean
    EngagementApi engagementApi;

    @Autowired
    SearchService searchService;

    @Autowired
    PeopleRepository people;

    @Autowired
    TestBeans.ModuleEventPublisher publisher;

    @Test
    void givenUserCreated_whenSearchUser_thenUserShouldBeSearchable() {
        publisher.publishEvent(new UserCreated(UUID.randomUUID(), "alice", "alice", "male", Instant.now(), "vcl.jpg"));

        assertThat(searchService.searchUsers("alice"))
            .hasSize(1)
            .extracting(PeopleResponse::name)
            .contains("alice");
    }

    @Test
    void givenUserAlrCreated_whenUserUpdated_thenSearchedUserMustBeUpdated() {
        UUID userId = UUID.randomUUID();
        publisher.publishEvent(new UserCreated(userId, "alice", "alice", "male", Instant.now(), "vcl.jpg"));
        publisher.publishEvent(new ProfileChanged(userId, "alice", "bob", "male", Instant.now(), "vcl.jpg"));


        assertThat(people.findByUserId(userId))
            .isPresent().get().extracting(Person::name).isEqualTo("bob");
        assertThat(searchService.searchUsers("bob"))
            .hasSize(1)
            .extracting(PeopleResponse::name)
            .contains("bob");
    }

    @Test
    void givenTextAdded_whenSearchText_thenTextShouldBeSearchable() {
        UUID sender = UUID.randomUUID();
        publisher.publishEvent(new TextAdded(
            1L,
            "vcl",
            "chatid",
            Instant.now(),
            UUID.randomUUID(),
            sender
        ));
        Mockito.when(engagementApi.canRead("chatid", sender))
            .thenReturn(true);
        assertThat(searchService.searchMessages("chatid", sender, "vcl"))
            .hasSize(1)
            .extracting(MessageResponse::getContent)
            .contains("vcl");

    }
}
