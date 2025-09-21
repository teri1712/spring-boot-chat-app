package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.repositories.ChatRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatRepository extends ChatRepository, JpaRepository<Chat, ChatIdentifier> {

        @Override
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT c FROM Chat c WHERE c.identifier = :id")
        Chat findByIdWithPessimisticLock(@Param("id") ChatIdentifier id);
}
