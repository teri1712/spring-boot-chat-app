package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.entities.ChatEvent;

public interface MessagePolicy {
    boolean isMessage(ChatEvent event);
}
