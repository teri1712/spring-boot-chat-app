package com.decade.practice.inbox.unit;

import com.decade.practice.inbox.application.events.BatchUpdateLogSaver;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.MessageState;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.TextState;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.MessageStateResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchUpdateLogSaverTest {

    @Mock
    ConversationRepository conversations;

    @Mock
    LogRepository logs;

    @Mock
    DeliveryService deliveryService;

    @Mock
    MessageStateResponseMapper messageStateMapper;

    @Mock
    ConversationInfoService conversationInfoService;

    LogBroadCaster broadcaster;

    BatchUpdateLogSaver saver;

    @BeforeEach
    void setUp() {
        broadcaster = new LogBroadCaster(logs, conversations, deliveryService, messageStateMapper, conversationInfoService);
        saver = new BatchUpdateLogSaver(broadcaster, conversations);
    }

    @Test
    void givenBatchUpdateEvent_whenOn_thenPublisherPublishEventForEachOwner() {
        // Given
        UUID senderId = UUID.randomUUID();
        String chatId = "chat123";
        MessageState state = TextState.builder()
            .sequenceId(1L)
            .postingId(UUID.randomUUID())
            .senderId(senderId)
            .chatId(chatId)
            .createdAt(Instant.now())
            .seenByIds(Set.of())
            .content("updated hello")
            .build();

        MessageUpdated messageUpdated = new MessageUpdated(
            1L, chatId, UUID.randomUUID(), senderId, Instant.now(), state
        );
        BatchUpdateEvent event = new BatchUpdateEvent(0, 100, messageUpdated);

        UUID owner1 = UUID.randomUUID();
        Room room = new Room(chatId, senderId, "Room", null, Set.of(owner1, senderId));

        Conversation convo1 = new Conversation(owner1, 1L, 0);
        convo1.addRecent(state); // Pre-add to test update

        List<ConversationView> views = List.of(
            new ConversationView(convo1, room)
        );

        when(conversations.findByChatIdBetweenParticipantIndex(eq(chatId), any(), any()))
            .thenReturn(views);

        // When
        saver.on(event);

        // Then
        ArgumentCaptor<InboxLogMessage> captor = ArgumentCaptor.forClass(InboxLogMessage.class);
        verify(deliveryService, times(1)).send(captor.capture());

        InboxLogMessage message = captor.getValue();
        assertThat(message.ownerId()).isEqualTo(owner1);

        ArgumentCaptor<List> conversationCaptor = ArgumentCaptor.forClass(List.class);
        verify(conversations, times(1)).saveAll(conversationCaptor.capture());
        assertThat(conversationCaptor.getValue()).hasSize(1);

        ArgumentCaptor<List> logCaptor = ArgumentCaptor.forClass(List.class);
        verify(logs, times(1)).saveAll(logCaptor.capture());
        assertThat(logCaptor.getValue()).hasSize(1);
    }
}
