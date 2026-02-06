package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.TypeEventDto;

public interface EventSender {
    void send(EventDetails event);

    void send(TypeEventDto typeEvent);
}
