package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public abstract class EventConverter<E extends ChatEvent> {
    protected abstract EventDto doConvert(E event);

    public abstract Class<E> supports();

    public EventDto convert(ChatEvent event) {

        if (event instanceof HibernateProxy proxy) {
            event = (E) Hibernate.unproxy(proxy);
        }

        if (!supports().isAssignableFrom(event.getClass())) {
            return null;
        }
        return doConvert((E) event);
    }

}
