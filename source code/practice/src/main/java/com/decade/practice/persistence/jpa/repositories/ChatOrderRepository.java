package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatOrder;
import com.decade.practice.persistence.jpa.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatOrderRepository extends JpaRepository<ChatOrder, Long> {


    @EntityGraph(attributePaths = {"chat"})
    Optional<ChatOrder> findByChat_IdentifierAndOwner(ChatIdentifier chatIdentifier, User owner);

    Optional<ChatOrder> findByChatAndOwner(Chat chat, User owner);

    @EntityGraph(attributePaths = {"chat"})
    List<ChatOrder> findByOwnerAndCurrentVersionLessThan(User owner, int currentVersion, Pageable pageable);
}
