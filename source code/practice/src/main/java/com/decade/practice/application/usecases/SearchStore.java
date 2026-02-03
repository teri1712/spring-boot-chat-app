package com.decade.practice.application.usecases;

import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.events.UserCreatedEvent;

public interface SearchStore {
    void save(UserCreatedEvent userCreatedEvent);

    void save(MessageCreatedEvent messageCreatedEvent);
}
