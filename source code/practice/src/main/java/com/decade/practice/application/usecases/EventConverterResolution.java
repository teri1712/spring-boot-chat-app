package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventResponse;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConverterResolution implements InitializingBean {


    private final List<EventConverter<? extends ChatEvent>> eventConverters;
    private final Map<Class<? extends ChatEvent>, EventConverter<? extends ChatEvent>> eventConverterMap = new HashMap<>();


    public EventResponse convert(ChatEvent chatEvent) {
        Class<? extends ChatEvent> eventType = Hibernate.getClass(chatEvent);
        EventConverter<ChatEvent> eventConverter = (EventConverter<ChatEvent>) eventConverterMap.get(eventType);
        if (eventConverter == null) {
            log.error("No converter found for event type: {}", eventType);
        }
        return eventConverter == null ? null : eventConverter.convert(chatEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (EventConverter<? extends ChatEvent> eventConverter : eventConverters) {
            eventConverterMap.put(eventConverter.supports(), eventConverter);
        }
    }

}
