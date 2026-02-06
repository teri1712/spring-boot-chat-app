package com.decade.practice.unit;

import com.decade.practice.application.services.ChatEventStore;
import com.decade.practice.application.services.DeliveryServiceImpl;
import com.decade.practice.application.usecases.EventConverterResolution;
import com.decade.practice.application.usecases.EventSender;
import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private ChatEventStore eventStore;
    @Mock
    private EventSender eventSender;
    @Mock
    private EventRepository eventRepo;
    @Mock
    private EventConverterResolution converterResolution;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @Test
    void givenValidRequest_whenCreateAndSend_thenEventsAreStoredAndSent() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();

        EventResponse myEvent = new EventResponse(
                null, idempotentKey, null,
                null, null, null, null, null, null,
                null, null, null, false, null, null, null
        );
        EventResponse yourEvent = new EventResponse(
                null, UUID.randomUUID(), null,
                null, null, null, null, null, null,
                null, null, null, false, null, null, null
        );

        EventDetails myDto = new EventDetails(myEvent, null);
        EventDetails yourDto = new EventDetails(yourEvent, null);

        given(eventStore.save(eq(senderId), eq(senderId), eq(idempotentKey), eq(chatIdentifier), eq(request)))
                .willReturn(List.of(myDto, yourDto));

        EventDetails result = deliveryService.createAndSend(senderId, chatIdentifier, idempotentKey, request);

        assertNotNull(result);
        assertEquals(myDto, result);

        verify(eventStore).save(eq(senderId), eq(senderId), eq(idempotentKey), eq(chatIdentifier), eq(request));
        verify(eventSender).send(myDto);
        verify(eventSender).send(yourDto);
    }

    @Test
    void givenChatNotExists_whenCreateAndSend_thenDelegatesToEventStore() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();

        EventResponse myEvent = new EventResponse(
                null, idempotentKey, null,
                null, null, null, null, null, null,
                null, null, null, false, null, null, null
        );
        EventDetails myDto = new EventDetails(myEvent, null);

        given(eventStore.save(eq(senderId), eq(senderId), eq(idempotentKey), eq(chatIdentifier), eq(request)))
                .willReturn(List.of(myDto));

        deliveryService.createAndSend(senderId, chatIdentifier, idempotentKey, request);

        verify(eventStore, times(1)).save(eq(senderId), eq(senderId), eq(idempotentKey), eq(chatIdentifier), eq(request));
    }

    @Test
    void givenDuplicateEvent_whenCreateAndSend_thenExistingEventIsReturned() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();

        given(eventStore.save(
                eq(senderId),
                eq(senderId),
                eq(idempotentKey),
                eq(chatIdentifier),
                eq(request)))
                .willThrow(new DataIntegrityViolationException("Duplicate"));

        ChatEvent persisted = mock(ChatEvent.class);
        Chat chat = mock(Chat.class);
        when(chat.getIdentifier()).thenReturn(chatIdentifier);
        when(chat.getFirstUser()).thenReturn(mock(User.class));
        when(persisted.getChat()).thenReturn(chat);
        when(persisted.getOwner()).thenReturn(mock(User.class));


        given(eventRepo.findByIdempotentKey(idempotentKey)).willReturn(Optional.of(persisted));

        EventResponse persistedResponse = new EventResponse(
                null, idempotentKey, null,
                null, null, null, null, null, null,
                null, null, null, false, null, null, null
        );
        given(converterResolution.convert(persisted)).willReturn(persistedResponse);

        EventDetails result = deliveryService.createAndSend(senderId, chatIdentifier, idempotentKey, request);

        assertEquals(persistedResponse, result.event());
    }
}
