package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ChatRepository extends CrudRepository<Chat, ChatIdentifier> {

    @Query("SELECT c FROM Chat c WHERE c.identifier = :id")
    Chat findDetailsById(ChatIdentifier id);
}