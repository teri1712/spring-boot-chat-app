package com.decade.practice.database.repository;

import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for Chat entities.
 */
public interface ChatRepository extends JpaRepository<Chat, ChatIdentifier> {

      @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
      @Override
      Optional<Chat> findById(ChatIdentifier chatIdentifier);
}