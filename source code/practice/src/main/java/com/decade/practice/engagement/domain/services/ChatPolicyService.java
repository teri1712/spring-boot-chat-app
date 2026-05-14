package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class ChatPolicyService {

    private final ParticipantRepository participants;

    public void apply(Set<Participant> p, Chat chat) {
        if (participants.countByParticipantId_ChatId(chat.getChatId()) + p.size() >= chat.getMaxParticipants()) {
            throw new ChatCapacityReachedException();
        }
    }

}
