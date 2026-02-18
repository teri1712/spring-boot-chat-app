package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.domain.ChatEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


public interface EventRepository extends CrudRepository<ChatEvent, UUID> {

    List<ChatEvent> findByOwnerIdAndChatIdAndEventVersionLessThanEqual(UUID ownerId, String chatId, int eventVersion, Pageable pageable);

    List<ChatEvent> findByOwnerIdAndEventVersionLessThanEqual(UUID ownerId, int eventVersion, Pageable pageable);

}