package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.entities.Chat;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


public interface ChatRepository extends CrudRepository<Chat, String> {

    boolean existsByIdentifierAndParticipants_Id(String chatId, UUID participantId);
}