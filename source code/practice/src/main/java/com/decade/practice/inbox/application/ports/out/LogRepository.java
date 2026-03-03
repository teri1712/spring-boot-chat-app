package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.LogWithConversation;
import com.decade.practice.inbox.domain.InboxLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<InboxLog, Long> {

      // TODO: Pagination
      List<InboxLog> findByOwnerIdAndChatIdAndSequenceIdGreaterThanEqual(UUID ownerId, String chatId, Long sequenceId, Pageable pageable);

      @Query("select new com.decade.practice.inbox.application.ports.out.projection.LogWithConversation(l,c) " +
                "from InboxLog l join Conversation c " +
                "on l.chatId = c.conversationId.chatId and l.ownerId = c.conversationId.ownerId " +
                "where l.sequenceId >= :sequenceId and l.ownerId = :ownerId")
      List<LogWithConversation> findByOwnerIdAndSequenceIdGreaterThanEqual(UUID ownerId, Long sequenceId, Pageable pageable);


}
