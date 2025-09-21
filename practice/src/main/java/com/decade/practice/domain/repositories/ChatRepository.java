package com.decade.practice.domain.repositories;

import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ChatRepository extends CrudRepository<Chat, ChatIdentifier> {

        Chat findByIdWithPessimisticLock(ChatIdentifier id);
}