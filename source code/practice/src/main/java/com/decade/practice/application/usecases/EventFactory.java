package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.ChatEvent;

public interface EventFactory<E extends ChatEvent> {

    E newInstance(EventRequest eventRequest);

    boolean supports(EventRequest eventRequest);
}
