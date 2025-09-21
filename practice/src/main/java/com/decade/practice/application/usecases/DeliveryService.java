package com.decade.practice.application.usecases;

import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;

public interface DeliveryService {
        <E extends ChatEvent> E createAndSend(User from, E event);
}
