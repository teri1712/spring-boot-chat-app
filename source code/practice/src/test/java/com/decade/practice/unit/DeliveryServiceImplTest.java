package com.decade.practice.unit;

import com.decade.practice.application.services.DeliveryServiceImpl;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventFactory;
import com.decade.practice.application.usecases.EventSender;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.dto.ChatDto;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.TextEventDto;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private EventStore eventStore;
    @Mock
    private EventSender eventSender;
    @Mock
    private ChatService chatService;
    @Mock
    private ChatRepository chatRepo;
    @Mock
    private EventRepository eventRepo;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private EntityManager em;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(deliveryService, "em", em);
    }

    @Test
    void givenValidRequest_whenCreateAndSend_thenEventsAreStoredAndSent() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();
        request.setSender(senderId);
        request.setChatIdentifier(chatIdentifier);

        EventFactory<ChatEvent> factory = mock(EventFactory.class);
        ChatEvent mine = mock(ChatEvent.class);
        ChatEvent yours = mock(ChatEvent.class);
        given(factory.createEvent(request)).willReturn(mine, yours);
        given(mine.getChatIdentifier()).willReturn(chatIdentifier);

        Chat chat = mock(Chat.class);
        given(chatRepo.findByIdWithPessimisticLock(chatIdentifier)).willReturn(chat);
        given(chat.getMessageCount()).willReturn(0);

        EventDto myDto = new EventDto();
        myDto.setIdempotencyKey(idempotentKey);
        myDto.setTextEvent(new TextEventDto("hello"));
        myDto.setChat(new ChatDto(chatIdentifier, senderId));

        EventDto yourDto = new EventDto();
        yourDto.setIdempotencyKey(idempotentKey);
        yourDto.setTextEvent(new TextEventDto("hello"));
        yourDto.setChat(new ChatDto(chatIdentifier, senderId));

        given(factory.createEventDto(mine)).willReturn(myDto);
        given(factory.createEventDto(yours)).willReturn(yourDto);

        EventDto result = deliveryService.createAndSend(idempotentKey, request, factory);

        assertNotNull(result);
        assertEquals(myDto, result);

        verify(eventStore).save(eq(senderId), eq(senderId), eq(mine));
        verify(eventStore).save(eq(senderId), eq(receiverId), eq(yours));
        verify(eventSender).send(myDto);
        verify(eventSender).send(yourDto);
        verify(eventPublisher, times(2)).publishEvent(any(EventDto.class));
        verify(eventPublisher, times(2)).publishEvent(any(MessageCreatedEvent.class));
    }

    @Test
    void givenChatNotExists_whenCreateAndSend_thenChatIsCreated() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();
        request.setSender(senderId);
        request.setChatIdentifier(chatIdentifier);

        EventFactory<ChatEvent> factory = mock(EventFactory.class);
        ChatEvent mine = mock(ChatEvent.class);
        ChatEvent yours = mock(ChatEvent.class);
        given(factory.createEvent(request)).willReturn(mine, yours);
        given(mine.getChatIdentifier()).willReturn(chatIdentifier);

        Chat chat = mock(Chat.class);
        given(chatRepo.findByIdWithPessimisticLock(chatIdentifier)).willReturn(null, chat);
        given(chat.getMessageCount()).willReturn(0);

        given(factory.createEventDto(any())).willReturn(new EventDto());

        deliveryService.createAndSend(idempotentKey, request, factory);

        verify(chatService).createChat(chatIdentifier);
    }

    @Test
    void givenDuplicateEvent_whenCreateAndSend_thenExistingEventIsReturned() {
        UUID idempotentKey = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatIdentifier chatIdentifier = ChatIdentifier.from(senderId, receiverId);

        EventRequest request = new EventRequest();
        request.setSender(senderId);
        request.setChatIdentifier(chatIdentifier);

        EventFactory<ChatEvent> factory = mock(EventFactory.class);
        ChatEvent mine = mock(ChatEvent.class);
        given(factory.createEvent(request)).willReturn(mine);
        given(mine.getChatIdentifier()).willReturn(chatIdentifier);
        given(mine.getIdempotentKey()).willReturn(idempotentKey);

        given(chatRepo.findByIdWithPessimisticLock(chatIdentifier)).willThrow(new ConstraintViolationException("Duplicate", null, null));

        ChatEvent persisted = mock(ChatEvent.class);
        given(eventRepo.findByIdempotentKey(idempotentKey)).willReturn(Optional.of(persisted));

        EventDto persistedDto = new EventDto();
        given(factory.createEventDto(persisted)).willReturn(persistedDto);

        EventDto result = deliveryService.createAndSend(idempotentKey, request, factory);

        assertEquals(persistedDto, result);
    }
}
