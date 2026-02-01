package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.UserCreatedEvent;

public interface SearchStore {
    void save(UserCreatedEvent userCreatedEvent);

    void save(EventDto eventDto);
}
