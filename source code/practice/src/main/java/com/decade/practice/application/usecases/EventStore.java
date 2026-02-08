package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDetails;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStore {
    EventDetails save(ChatEvent chatEvent);
}