package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventFactoryResolution {


    private final List<EventFactory<? extends ChatEvent>> eventFactory;

    public Optional<ChatEvent> newInstance(EventRequest eventRequest) {
        for (EventFactory<? extends ChatEvent> factory : eventFactory) {
            if (factory.supports(eventRequest)) {
                return Optional.of(factory.newInstance(eventRequest));
            }
        }
        return Optional.empty();
    }


}
