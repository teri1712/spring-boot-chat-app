package com.decade.practice.domain.repositories;

import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface EventRepository extends CrudRepository<ChatEvent, UUID> {

        List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(
                User owner,
                Chat chat,
                int eventVersion,
                Pageable pageable
        );

        List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(
                User owner,
                int eventVersion,
                Pageable pageable
        );

        ChatEvent findFirstByOwnerOrderByEventVersionDesc(
                User owner
        );
}