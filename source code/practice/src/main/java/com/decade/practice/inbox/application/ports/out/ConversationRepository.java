package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.ConversationId;
import com.decade.practice.inbox.domain.HashValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, ConversationId> {


      Optional<Conversation> findFirstByHash(HashValue hash);

      List<Conversation> findByConversationId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(UUID ownerId, Instant modifiedAt, Pageable pageable);

      List<Conversation> findByConversationId_ChatId(String chatId);

      @Modifying
      @Query("update Conversation c set c.roomName = :roomName, c.roomAvatar = :roomAvatar where c.conversationId.chatId = :chatId")
      long updateRoomNameAndRoomAvatar(String chatId, String roomName, String roomAvatar);
}
