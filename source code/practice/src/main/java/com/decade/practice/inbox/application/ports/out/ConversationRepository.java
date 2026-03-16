package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.ConversationId;
import com.decade.practice.inbox.domain.HashValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, ConversationId> {


      Optional<Conversation> findFirstByHash(HashValue hash);
      
      @Query("select new com.decade.practice.inbox.application.ports.out.projection.ConversationView(c,r) " +
                "from Conversation c " +
                "join fetch Room r on r.chatId = c.conversationId.chatId " +
                "where c.modifiedAt <= :modifiedAt and c.conversationId.ownerId = :ownerId")
      List<ConversationView> findByConversationId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(UUID ownerId, Instant modifiedAt, Pageable pageable);

      @Query("select new com.decade.practice.inbox.application.ports.out.projection.ConversationView(c,r) " +
                "from Conversation c " +
                "join fetch Room r on r.chatId = c.conversationId.chatId " +
                "where r.chatId = :chatId")
      List<ConversationView> findByConversationId_ChatId(String chatId);

}
