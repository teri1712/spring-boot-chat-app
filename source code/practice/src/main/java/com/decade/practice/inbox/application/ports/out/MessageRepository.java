package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

      Optional<Message> findFirstByChatIdOrderBySequenceIdDesc(String chatId);

      @Query("select distinct m from Message m join m.seenPointers p join fetch m.seenPointers pp " +
                "where p.senderId = :senderId and p.chatId = :chatId")
      Optional<Message> findByLastSeen(String chatId, UUID senderId);

      List<Message> findByChatIdAndSequenceIdLessThanEqual(String chatId, Long sequenceId, Pageable pageable);


}
