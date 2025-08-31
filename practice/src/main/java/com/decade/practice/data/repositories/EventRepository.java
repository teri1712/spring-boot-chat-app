package com.decade.practice.data.repositories;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(
        readOnly = true,
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED
)
public interface EventRepository extends JpaRepository<ChatEvent, UUID> {

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

        @EntityGraph(attributePaths = {"chat", "chat.firstUser", "chat.secondUser", "edges"}, type = EntityGraph.EntityGraphType.FETCH)
        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );

        ChatEvent getByLocalId(UUID localId);
}