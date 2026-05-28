package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.LogView;
import com.decade.practice.inbox.domain.InboxLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<InboxLog, Long> {


    @Query("select distinct new com.decade.practice.inbox.application.ports.out.projection.LogView(l,m, new com.decade.practice.inbox.application.ports.out.projection.ConversationView(c,r)) " +
        "from InboxLog l " +
        "join fetch Conversation c on l.conversationId = c.id " +
        "join fetch Room r on r.id = c.roomId " +
        "join fetch Message m on l.messageId = m.sequenceId " +
        "where r.chatId = :chatId and c.ownerId = :ownerId and l.sequenceId >= :sequenceId")
    List<LogView> findByOwnerIdAndChatIdAndSequenceIdGreaterThanEqual(UUID ownerId, String chatId, Long sequenceId, Pageable pageable);

    @Query("select distinct new com.decade.practice.inbox.application.ports.out.projection.LogView(l,m, new com.decade.practice.inbox.application.ports.out.projection.ConversationView(c,r)) " +
        "from InboxLog l " +
        "join fetch Conversation c on l.conversationId = c.id " +
        "join fetch Room r on r.id = c.roomId " +
        "join fetch Message m on l.messageId = m.sequenceId " +
        "where l.sequenceId >= :sequenceId and l.ownerId = :ownerId")
    List<LogView> findByOwnerIdAndSequenceIdGreaterThanEqual(UUID ownerId, Long sequenceId, Pageable pageable);

    List<InboxLog> findBySenderId(UUID senderId);

}
