package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatRepository extends CrudRepository<Chat, String> {
    
}