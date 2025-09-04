package com.decade.practice.data.repositories;

import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.ChatEvent;
import com.decade.practice.models.domain.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
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

        @QueryHints({
                @QueryHint(name = "org.hibernate.cacheable", value = "true")
        })
        List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(
                User owner,
                Chat chat,
                int eventVersion,
                Pageable pageable
        );

        @QueryHints({
                @QueryHint(name = "org.hibernate.cacheable", value = "true")
        })
        List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(
                User owner,
                int eventVersion,
                Pageable pageable
        );

        @EntityGraph(attributePaths = {"chat", "chat.firstUser", "chat.secondUser", "edges"}, type = EntityGraph.EntityGraphType.FETCH)
        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );

        ChatEvent findByLocalId(UUID localId);
}