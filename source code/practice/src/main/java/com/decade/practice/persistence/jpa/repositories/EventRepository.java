package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.entities.ChatEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface EventRepository extends CrudRepository<ChatEvent, UUID> {

    @EntityGraph(attributePaths = {"chat", "chat.firstCreator", "chat.secondCreator"})
    List<ChatEvent> findByOwner_IdAndChat_IdentifierAndEventVersionLessThanEqual(UUID ownerId, String chatId, int eventVersion, Pageable pageable);

    @EntityGraph(attributePaths = {"chat", "chat.firstCreator", "chat.secondCreator"})
    List<ChatEvent> findByOwner_IdAndEventVersionLessThanEqual(UUID ownerId, int eventVersion, Pageable pageable);

    @EntityGraph(attributePaths = {"chat", "chat.firstCreator", "chat.secondCreator"})
    Optional<ChatEvent> findFirstByOwner_IdOrderByEventVersionDesc(UUID ownerId);

    @EntityGraph(attributePaths = {"chat", "owner", "sender"})
    Optional<ChatEvent> findByIdempotentKey(UUID idempotentKey);
}