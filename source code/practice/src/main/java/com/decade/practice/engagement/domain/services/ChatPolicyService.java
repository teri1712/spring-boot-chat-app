package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatPolicyService {

      private final ParticipantRepository participants;

      public void apply(Participant participant, Chat policy) {
            if (participants.countByParticipantId_ChatId(policy.getChatId()) >= policy.getMaxParticipants()) {
                  throw new ChatCapacityReachedException();
            }
      }

}
