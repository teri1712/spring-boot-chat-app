package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.api.EngagementFacade;
import com.decade.practice.engagement.api.EngagementRule;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.ParticipantPolicy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EngagementFacadeImpl implements EngagementFacade {

    private final ParticipantRepository participants;

    @Override
    public EngagementRule find(String chatId, UUID userId) {
        Participant participant = participants.findById(new ParticipantId(userId, chatId)).orElseThrow();
        ParticipantPolicy policy = participant.getParticipantPolicy();
        return new EngagementRule(userId, chatId, policy.write(), policy.read());
    }
}
