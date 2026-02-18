package com.decade.practice.engagement.application.events;


import com.decade.practice.engagement.api.events.ChatSnapshot;
import com.decade.practice.engagement.api.events.EventPlacedMapper;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.Preference;
import com.decade.practice.engagement.domain.events.ParticipantPlaced;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public abstract class AbstractParticipantEventPlacedListener<E extends ParticipantPlaced> {

    protected final ChatRepository chats;
    protected final ParticipantRepository participants;
    protected final ApplicationEventPublisher applicationEventPublisher;
    protected final EngagementPolicy engagementPolicy;

    // My bad, I'm lazy
    protected final EventPlacedMapper mapper;

    @EventListener
    public void on(E eventPlaced) {

        ParticipantId participantId = new ParticipantId(eventPlaced.getSenderId(), eventPlaced.getChatId());
        String chatId = participantId.chatId();

        Participant participant = participants.findById(participantId).orElseThrow();
        Chat chat = chats.findById(chatId).orElseThrow();
        engagementPolicy.applyWrite(participant, chat);
        chat.incrementInteractionCount();

        handle(eventPlaced);

        Preference preference = chat.getPreference();
        String roomName = preference.roomName();
        String roomAvatar = preference.roomAvatar();
        List<UUID> peers = participants.findByChatId(chatId);
        ChatSnapshot snapshot = new ChatSnapshot(chatId, roomName, roomAvatar, peers);


        // TODO: Handle fanout
        applicationEventPublisher.publishEvent(mapper.toIntegration(eventPlaced, snapshot));
    }


    protected void handle(E eventPlaced) {
    }
}
