package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.application.ports.out.projection.EventWithHistory;
import com.decade.practice.threads.domain.ChatEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface EventRepository extends CrudRepository<ChatEvent, UUID> {

    @Query("select new com.decade.practice.threads.application.ports.out.projection.EventWithHistory(e,h) from ChatEvent e " +
            "join ChatHistory h on e.chatId = h.chatHistoryId.chatId and e.ownerId = h.chatHistoryId.ownerId " +
            "where e.ownerId = :ownerId and e.chatId = :chatId and e.eventVersion <= :eventVersion")
    List<EventWithHistory> findByOwnerIdAndChatIdAndEventVersionLessThanEqual(UUID ownerId, String chatId, int eventVersion, Pageable pageable);


    @Query("select new com.decade.practice.threads.application.ports.out.projection.EventWithHistory(e,h) from ChatEvent e " +
            "join ChatHistory h on e.chatId = h.chatHistoryId.chatId and e.ownerId = h.chatHistoryId.ownerId " +
            "where e.ownerId = :ownerId and e.eventVersion <= :eventVersion")
    List<EventWithHistory> findByOwnerIdAndEventVersionLessThanEqual(UUID ownerId, int eventVersion, Pageable pageable);


    @Query("select new com.decade.practice.threads.application.ports.out.projection.EventWithHistory(e,h) from ChatEvent e " +
            "join ChatHistory h on e.chatId = h.chatHistoryId.chatId and e.ownerId = h.chatHistoryId.ownerId " +
            "where e.id = :id")
    Optional<EventWithHistory> findSummaryById(UUID id);
}