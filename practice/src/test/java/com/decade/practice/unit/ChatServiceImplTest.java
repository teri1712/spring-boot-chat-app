package com.decade.practice.unit;

import com.decade.practice.api.dto.ChatDetailsDto;
import com.decade.practice.api.dto.ChatSnapshot;
import com.decade.practice.application.exception.OutdatedVersionException;
import com.decade.practice.application.services.ChatServiceImpl;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.SyncContext;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private EventService eventService;
    @Mock
    private ChatRepository chatRepo;
    @Mock
    private ChatOrderRepository chatOrderRepo;
    @Mock
    private EntityManager em;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatService, "em", em);
    }

    @Test
    void givenExistingChatIdentifier_whenGetOrCreateChat_thenChatIsReturned() {
        ChatIdentifier identifier = new ChatIdentifier(UUID.randomUUID(), UUID.randomUUID());
        Chat chat = mock(Chat.class);
        given(chatRepo.findById(identifier)).willReturn(Optional.of(chat));

        Chat result = chatService.getOrCreateChat(identifier);

        assertEquals(chat, result);
        verify(chatRepo, times(1)).findById(identifier);
    }

    @Test
    void givenChatIdentifier_whenCreateChat_thenChatIsCreated() {
        UUID u1Id = UUID.randomUUID();
        UUID u2Id = UUID.randomUUID();
        if (u1Id.compareTo(u2Id) > 0) {
            UUID temp = u1Id;
            u1Id = u2Id;
            u2Id = temp;
        }
        ChatIdentifier identifier = new ChatIdentifier(u1Id, u2Id);
        User u1 = new User();
        u1.setId(u1Id);
        u1.setUsername("user1");
        User u2 = new User();
        u2.setId(u2Id);
        u2.setUsername("user2");

        given(userRepo.findById(u1Id)).willReturn(Optional.of(u1));
        given(userRepo.findById(u2Id)).willReturn(Optional.of(u2));
        doAnswer(invocation -> invocation.getArgument(0)).when(em).merge(any());

        Chat result = chatService.createChat(identifier);

        assertNotNull(result);
        assertEquals(u1Id, result.getFirstUser().getId());
        assertEquals(u2Id, result.getSecondUser().getId());
        verify(em).merge(any(Chat.class));
    }

    @Test
    void givenOutdatedVersion_whenListChat_thenThrowOutdatedVersionException() {
        UUID userId = UUID.randomUUID();
        User owner = new User();
        owner.setId(userId);
        SyncContext syncContext = new SyncContext(owner);
        syncContext.setEventVersion(5);
        owner.setSyncContext(syncContext);

        given(userRepo.findById(userId)).willReturn(Optional.of(owner));

        assertThrows(OutdatedVersionException.class, () ->
                chatService.listChat(userId, 4, Optional.empty(), 10)
        );
    }

    @Test
    void givenChatIdentifierAndUserId_whenGetSnapshot_thenChatSnapshotIsReturned() {
        ChatIdentifier identifier = new ChatIdentifier(UUID.randomUUID(), UUID.randomUUID());
        UUID userId = identifier.getFirstUser();
        User owner = new User();
        owner.setId(userId);
        owner.setUsername("owner");
        User partner = new User();
        partner.setId(identifier.getSecondUser());
        partner.setUsername("partner");
        Chat chat = new Chat(owner, partner);
        chat.setIdentifier(identifier);

        given(userRepo.findById(userId)).willReturn(Optional.of(owner));
        given(chatRepo.findById(identifier)).willReturn(Optional.of(chat));
        given(eventService.findByOwnerAndChatAndEventVersionLessThanEqual(any(), any(), anyInt()))
                .willReturn(new ArrayList<>());

        ChatSnapshot result = chatService.getSnapshot(identifier, userId, 10);

        assertNotNull(result);
        assertEquals(10, result.getAtVersion());
        verify(eventService).findByOwnerAndChatAndEventVersionLessThanEqual(userId, identifier, 10);
    }

    @Test
    void givenChatIdentifierAndUserId_whenGetDetails_thenChatDetailsDtoIsReturned() {
        UUID userId = UUID.randomUUID();
        User owner = new User();
        owner.setId(userId);
        owner.setUsername("owner");
        User partner = new User();
        UUID partnerId = UUID.randomUUID();
        partner.setId(partnerId);
        partner.setUsername("partner");

        ChatIdentifier identifier = ChatIdentifier.from(userId, partnerId);

        Chat chat = new Chat(owner, partner);
        chat.setIdentifier(identifier);

        given(userRepo.findById(userId)).willReturn(Optional.of(owner));
        given(chatRepo.findById(identifier)).willReturn(Optional.of(chat));

        ChatDetailsDto result = chatService.getDetails(identifier, userId);

        assertNotNull(result);
    }
}
