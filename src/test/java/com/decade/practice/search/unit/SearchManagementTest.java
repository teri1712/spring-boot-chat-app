package com.decade.practice.search.unit;


import com.decade.practice.inbox.domain.events.TextAdded;
import com.decade.practice.search.application.events.SearchManagement;
import com.decade.practice.search.application.ports.out.HistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.MessageHistory;
import com.decade.practice.search.domain.Person;
import com.decade.practice.users.domain.events.ProfileChanged;
import com.decade.practice.users.domain.events.UserCreated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SearchManagementTest {


    @Mock
    PeopleRepository people;

    @Mock
    HistoryRepository messages;

    @Captor
    ArgumentCaptor<Person> personCaptor;

    @Captor
    ArgumentCaptor<MessageHistory> historyCaptor;

    @InjectMocks
    SearchManagement searchManagement;

    @Test
    void shouldSaveNewPersonWhenUserCreated() {
        searchManagement.on(new UserCreated(UUID.randomUUID(), "alice", "alisuh", "male", Instant.now(), "vcl.jpg"));
        Mockito.verify(people).save(personCaptor.capture());

        assertThat(personCaptor.getValue()).extracting(Person::name).isEqualTo("alisuh");
        assertThat(personCaptor.getValue()).extracting(Person::avatar).isEqualTo("vcl.jpg");
    }

    @Test
    void shouldUpdatePersonWhenProfileChanged() {
        UUID userId = UUID.randomUUID();
        Mockito.when(people.findByUserId(eq(userId)))
            .thenReturn(Optional.of(new Person(null, userId, "alice", "alice", "male", "vcl.jpg")));

        searchManagement.on(new ProfileChanged(userId, "alice", "alicesuh", "male", Instant.now(), "dcm.jpg"));

        Mockito.verify(people).save(personCaptor.capture());
        assertThat(personCaptor.getValue()).extracting(Person::name).isEqualTo("alicesuh");
        assertThat(personCaptor.getValue()).extracting(Person::avatar).isEqualTo("dcm.jpg");
    }

    @Test
    void shouldAddNewHistoryRecordWhenTextAdded() {
        UUID sender = UUID.randomUUID();
        searchManagement.on(new TextAdded(
            1L,
            "vcl",
            "chatid",
            Instant.now(),
            UUID.randomUUID(),
            sender
        ));
        Mockito.verify(messages).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue()).extracting(MessageHistory::content).isEqualTo("vcl");
        assertThat(historyCaptor.getValue()).extracting(MessageHistory::sequenceNumber).isEqualTo(1L);

    }
}
