package com.decade.practice.engagement.application.events;

import com.decade.practice.engagement.api.events.EventPlacedMapper;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.events.ImageParticipantPlaced;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ImageParticipantEventPlacedListener extends AbstractParticipantEventPlacedListener<ImageParticipantPlaced> {
    public ImageParticipantEventPlacedListener(ChatRepository chats, ParticipantRepository participants, ApplicationEventPublisher applicationEventPublisher, EngagementPolicy engagementPolicy, EventPlacedMapper mapper) {
        super(chats, participants, applicationEventPublisher, engagementPolicy, mapper);
    }
}
