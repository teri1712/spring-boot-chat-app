package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.api.EngagementRule;
import com.decade.practice.engagement.api.RuleNotFoundException;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.ParticipantPolicy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EngagementApiImpl implements EngagementApi {

      private final ParticipantRepository participants;

      @Override
      public EngagementRule find(String chatId, UUID userId) throws RuleNotFoundException {
            Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElseThrow(
                      RuleNotFoundException::new
            );
            ParticipantPolicy policy = participant.getParticipantPolicy();
            return new EngagementRule(userId, chatId, policy.write(), policy.read());
      }
}
