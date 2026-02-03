package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.TypeEventDto;

public interface EventSender {
    void send(EventDto event);

    void send(TypeEventDto typeEvent);
}
