package com.decade.practice.application.events;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.persistence.redis.TypeEvent;

public interface EventSender {
    void send(EventDto event);

    void send(TypeEvent typeEvent);
}
