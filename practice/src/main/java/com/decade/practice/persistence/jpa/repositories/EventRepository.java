package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface EventRepository extends CrudRepository<ChatEvent, UUID> {

    @EntityGraph(attributePaths = {"chat", "chat.firstUser", "chat.secondUser"})
    List<ChatEvent> findByOwner_IdAndChat_IdentifierAndEventVersionLessThanEqual(UUID ownerId, ChatIdentifier chatIdentifier, int eventVersion, Pageable pageable);

    @EntityGraph(attributePaths = {"chat", "chat.firstUser", "chat.secondUser"})
    List<ChatEvent> findByOwner_IdAndEventVersionLessThanEqual(UUID ownerId, int eventVersion, Pageable pageable);

    Optional<ChatEvent> findFirstByOwner_IdOrderByEventVersionDesc(UUID ownerId);

    Optional<ChatEvent> findByIdempotentKey(UUID idempotentKey);
}