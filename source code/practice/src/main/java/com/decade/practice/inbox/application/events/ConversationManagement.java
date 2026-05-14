package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.ParticipantAdded;
import com.decade.practice.inbox.apis.ConversationApi;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ConversationManagement implements ConversationApi {

    final ConversationRepository conversations;
    final RoomRepository rooms;

    @ApplicationModuleListener(id = "inbox-participant-added")
    void on(ParticipantAdded event) {
        Room room = rooms.findByChatId(event.chatId()).orElseThrow();
        List<Conversation> cL = event.participantIds().stream()
            .map(new Function<UUID, Conversation>() {
                @Override
                public Conversation apply(UUID participantId) {
                    room.incParticipantCount();
                    room.addRepresentative(participantId);
                    return new Conversation(participantId, room.getId(), room.getParticipantCount());
                }
            }).toList();
        conversations.saveAll(cL);
        rooms.save(room);
    }


    @Override
    public void create(String chatId, UUID caller, Set<UUID> participants, String name) {
        Room room = new Room(chatId, caller, name, null, participants);
        rooms.save(room);

        int index = 0;
        for (UUID participant : participants)
            conversations.save(new Conversation(participant, room.getId(), index++));

    }
}
