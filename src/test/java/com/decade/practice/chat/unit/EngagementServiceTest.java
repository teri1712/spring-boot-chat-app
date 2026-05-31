package com.decade.practice.chat.unit;

import com.decade.practice.engagement.api.mapper.ChatPolicyMapper;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ChatViewRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.application.services.EngagementServiceImpl;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.events.ParticipantAdded;
import com.decade.practice.engagement.domain.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngagementServiceTest {

    @Mock
    ParticipantRepository participants;
    @Mock
    ChatViewRepository chatViews;
    @Mock
    ChatRepository chats;

    @Mock
    ChatPolicyService chatPolicyService;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    GroupChatFactory groupChatFactory;
    @Mock
    DirectChatFactory directChatFactory;
    @Mock
    EngagementPolicy engagementPolicy;

    @Mock
    ChatPolicyMapper chatMapper;
    @Mock
    StalkPolicy stalkPolicy;

    @InjectMocks
    EngagementServiceImpl engagementService;


    @Captor
    ArgumentCaptor<Set<Participant>> pSetCaptor;

    @Captor
    ArgumentCaptor<ParticipantAdded> paCaptor;

    @Test
    void shouldGetChatFromWriteModelRepoAndSaveParticipantsAndCallChatPolicyServiceWhenAddParticipants() {
        String chatId = "12345678-1234-1234-1234-123456789012";

        Chat chat = mock(Chat.class);
        when(chats.findById(chatId))
            .thenReturn(Optional.of(chat));

        UUID participantId1 = UUID.randomUUID();
        UUID participantId2 = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(participantId1, participantId2);

        engagementService.add(chatId, participantIds);

        verify(chats).findById(chatId);
        verify(chatPolicyService).apply(any(), eq(chat));
        verify(participants).saveAll(pSetCaptor.capture());

        Set<Participant> participants = pSetCaptor.getValue();
        assertThat(participants)
            .hasSize(2)
            .extracting(Participant::getParticipantId)
            .extracting(ParticipantId::userId)
            .contains(participantId1, participantId2);

        
        verify(publisher).publishEvent(paCaptor.capture());
        ParticipantAdded added = paCaptor.getValue();
        assertThat(added.chatId()).isEqualTo(chatId);
        assertThat(added.participantIds()).isEqualTo(participantIds);
    }

    @Test
    void shouldThrowExceptionWhenChatNotFound() {
        String chatId = "12345678-1234-1234-1234-123456789012";

        when(chats.findById(chatId))
            .thenReturn(Optional.empty());

        UUID participantId1 = UUID.randomUUID();
        UUID participantId2 = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(participantId1, participantId2);

        assertThatThrownBy(() ->
            engagementService.add(chatId, participantIds)
        ).isInstanceOf(NoSuchElementException.class);
    }
}
