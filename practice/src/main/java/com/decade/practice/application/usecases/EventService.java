package com.decade.practice.application.usecases;

import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;

import java.util.List;

public interface EventService {

        List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(
                User owner,
                Chat chat,
                int eventVersion
        );

        List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(
                User owner,
                int eventVersion
        );

        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );
}
