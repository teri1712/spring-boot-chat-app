package com.decade.practice.data.repositories;

import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, ChatIdentifier> {

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT c FROM Chat c WHERE c.identifier = :id")
        Chat findByIdWithPessimisticLock(@Param("id") ChatIdentifier id);
}