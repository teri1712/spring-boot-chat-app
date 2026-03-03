package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.ParticipantPolicy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("engagementApi")
@AllArgsConstructor
public class EngagementApiImpl implements EngagementApi {

      private final ParticipantRepository participants;

      @Override
      public boolean canRead(String chatId, UUID userId) {
            return participants.findById(new ParticipantId(userId, chatId))
                      .map(Participant::getParticipantPolicy)
                      .map(ParticipantPolicy::read)
                      .orElse(false);
      }

      @Override
      public boolean canWrite(String chatId, UUID userId) {
            return participants.findById(new ParticipantId(userId, chatId))
                      .map(Participant::getParticipantPolicy)
                      .map(ParticipantPolicy::write)
                      .orElse(false);
      }
}
