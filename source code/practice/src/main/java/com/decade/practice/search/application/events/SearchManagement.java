package com.decade.practice.search.application.events;


import com.decade.practice.inbox.domain.events.TextAdded;
import com.decade.practice.search.application.ports.out.HistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.History;
import com.decade.practice.search.domain.Person;
import com.decade.practice.users.domain.events.ProfileChanged;
import com.decade.practice.users.domain.events.UserCreated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SearchManagement {

    private final HistoryRepository messages;
    private final PeopleRepository users;

    @ApplicationModuleListener(id = "search_user_created")
    public void on(UserCreated event) {
        log.trace("Received user: {}", event);

        users.save(new Person(
            null,
            event.userId(),
            event.username(),
            event.name(),
            event.gender(),
            event.avatar()));
    }

    @ApplicationModuleListener(id = "search_profile_changed")
    public void on(ProfileChanged event) {
        log.trace("Received user: {}", event);
        Person person = users.findByUserId(event.userId()).orElse(
            new Person(null, event.userId(), event.username(), event.name(), event.gender(), event.avatar())
        );
        users.save(new Person(
            person.id(),
            person.userId(),
            person.username(),
            person.name(),
            person.gender(),
            person.avatar()));
    }


    @ApplicationModuleListener(id = "search_text_created")
    public void on(TextAdded event) {
        log.trace("Received currentState: {}", event);
        messages.save(new History(
            null,
            event.text(),
            event.sequenceNumber(),
            event.chatId(),
            event.createdAt()));
    }


}
