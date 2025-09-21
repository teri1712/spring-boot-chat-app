package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.EventRepository;
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
public interface JpaEventRepository extends EventRepository, JpaRepository<ChatEvent, UUID> {

        @Override
        @QueryHints({
                @QueryHint(name = "org.hibernate.cacheable", value = "true")
        })
        List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(
                User owner,
                Chat chat,
                int eventVersion,
                Pageable pageable
        );

        @Override
        @QueryHints({
                @QueryHint(name = "org.hibernate.cacheable", value = "true")
        })
        List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(
                User owner,
                int eventVersion,
                Pageable pageable
        );

        @Override
        @EntityGraph(attributePaths = {"chat", "chat.firstUser", "chat.secondUser", "edges"}, type = EntityGraph.EntityGraphType.FETCH)
        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );

        @Override
        ChatEvent findByLocalId(UUID localId);
}