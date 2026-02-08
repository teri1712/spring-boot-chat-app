package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserThread {

    private final EventRepository eventRepository;

    public void registerEvent(ChatEvent event) {
        User owner = event.getOwner();
        owner.getSyncContext().incVersion();
        event.setEventVersion(owner.getSyncContext().getEventVersion());
        eventRepository.save(event);
    }
}
