package com.decade.practice.usecases;

import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.User;

public interface EventOperations {

        <E extends ChatEvent> E createAndSend(User from, E event);

}
