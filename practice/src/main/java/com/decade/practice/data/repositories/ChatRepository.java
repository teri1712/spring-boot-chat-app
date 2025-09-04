package com.decade.practice.data.repositories;

import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Chat entities.
 */
public interface ChatRepository extends JpaRepository<Chat, ChatIdentifier> {

}