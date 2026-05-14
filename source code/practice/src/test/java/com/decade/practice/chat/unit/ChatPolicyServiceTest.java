package com.decade.practice.chat.unit;

import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.services.ChatCapacityReachedException;
import com.decade.practice.engagement.domain.services.ChatPolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatPolicyServiceTest {

    @Mock
    ParticipantRepository participants;

    @InjectMocks
    ChatPolicyService chatPolicyService;

    @Test
    void givenGroupCapacityIs50Left_whenAdd51MoreMemberToChat_thenThrowException() {
        String chatId = "12345678-1234-1234-1234-123456789012";
        UUID callerId = UUID.randomUUID();

        final int maxCapacity = 1000;

        Chat chat = new Chat(chatId, maxCapacity, new ChatCreators(callerId, Set.of()));

        when(participants.countByParticipantId_ChatId(chatId))
            .thenReturn((long) (maxCapacity - 50));

        Set<Participant> newParticipants = new HashSet<>();
        for (int i = 0; i < 51; i++) {
            UUID participantId = UUID.randomUUID();
            Participant participant = new Participant(participantId, chatId);
            newParticipants.add(participant);
        }

        assertThatThrownBy(() -> {
            chatPolicyService.apply(newParticipants, chat);
        })
            .isInstanceOf(ChatCapacityReachedException.class);
    }
}
