package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

      @Query("select p.participantId.userId from Participant p where p.participantId.chatId = :chatId")
      List<UUID> findByChatId(String chatId);

      long countByParticipantId_ChatId(String chatId);
}
