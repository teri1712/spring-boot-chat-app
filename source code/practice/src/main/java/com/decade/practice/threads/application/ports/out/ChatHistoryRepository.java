package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.domain.ChatHistory;
import com.decade.practice.threads.domain.ChatHistoryId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, ChatHistoryId> {

    Stream<ChatHistory> findByChatHistoryId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(UUID ownerId, Instant modifiedAt, Pageable pageable);

}
