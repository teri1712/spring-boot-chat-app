package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.MessageEvent;
import org.springframework.stereotype.Service;

@Service
public class SubclassBasedMessagePolicy implements MessagePolicy {
    @Override
    public boolean isMessage(ChatEvent event) {
        return event instanceof MessageEvent;
    }
}
