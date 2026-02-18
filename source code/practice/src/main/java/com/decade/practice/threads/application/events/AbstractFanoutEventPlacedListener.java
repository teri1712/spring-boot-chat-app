package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ChatSnapshot;
import com.decade.practice.engagement.api.events.EventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.ChatEvent;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public abstract class AbstractFanoutEventPlacedListener<E extends EventPlaced> {

    protected final EventRepository events;

    public void on(E eventPlaced) {
        ChatSnapshot snapshot = eventPlaced.getSnapshot();

        List<ChatEvent> eventList = snapshot.participants().stream()
                .distinct()
                .map(participant ->
                        newInstance(eventPlaced, participant)).toList();
        events.saveAll(eventList);


    }

    protected abstract ChatEvent newInstance(E event, UUID ownerId);
}
