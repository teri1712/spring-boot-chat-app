package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventFactoryResolution implements InitializingBean {

    private final List<EventFactory<? extends ChatEvent>> eventFactories;
    private Map<Class<? extends ChatEvent>, EventFactory<? extends ChatEvent>> eventFactoryMap = new HashMap<>();

    public EventFactory<? extends ChatEvent> getFactory(Class<? extends ChatEvent> eventType) {
        EventFactory<? extends ChatEvent> eventFactory = eventFactoryMap.get(eventType);
        if (eventFactory != null) {
            return eventFactory;
        }
        if (eventType == ChatEvent.class)
            return null;
        Class<?> supper = eventType.getSuperclass();
        if (!ChatEvent.class.isAssignableFrom(supper)) {
            return null;
        }
        return getFactory((Class<? extends ChatEvent>) supper);
    }


    public EventDto mapToDto(ChatEvent chatEvent) {
        EventFactory<?> eventFactory = getFactory(Hibernate.getClass(chatEvent));
        return eventFactory.createEventDto(chatEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (EventFactory<? extends ChatEvent> eventFactory : eventFactories) {
            eventFactoryMap.put(eventFactory.getSupportedType(), eventFactory);
        }
    }
}
