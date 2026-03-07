package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantPolicy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class EngagementPolicy {

      public void applyRead(Participant participant, Chat chat) {
            if (chat == null)
                  return;
            if (participant == null)
                  throw new AccessDeniedException("You do not have permission to read");
            ParticipantPolicy policy = participant.getParticipantPolicy();
            if (!policy.read())
                  throw new AccessDeniedException("You do not have permission to read");
      }

      public void applyWrite(Participant participant, Chat chat) {
            if (chat == null)
                  return;
            if (participant == null)
                  throw new AccessDeniedException("You do not have permission to write");
            ParticipantPolicy policy = participant.getParticipantPolicy();
            if (!policy.write())
                  throw new AccessDeniedException("You do not have permission to write");
      }
}
