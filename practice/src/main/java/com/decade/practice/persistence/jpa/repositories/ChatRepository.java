package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ChatRepository extends CrudRepository<Chat, ChatIdentifier> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Chat c WHERE c.identifier = :id")
    Chat findByIdWithPessimisticLock(ChatIdentifier id);

    @EntityGraph(attributePaths = {"theme"})
    @Query("SELECT c FROM Chat c WHERE c.identifier = :id")
    Chat findDetailsById(ChatIdentifier id);
}