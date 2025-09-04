package com.decade.practice.usecases;

import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.ChatEvent;
import com.decade.practice.models.domain.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EventOperations {

        <E extends ChatEvent> E createAndSend(User from, E event);

        List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(
                User owner,
                Chat chat,
                int eventVersion,
                Pageable pageable
        );

        List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(
                User owner,
                int eventVersion,
                Pageable pageable
        );

        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );

        ChatEvent findByLocalId(UUID localId);
}
