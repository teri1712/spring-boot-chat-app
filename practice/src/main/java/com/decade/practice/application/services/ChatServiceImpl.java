package com.decade.practice.application.services;

import com.decade.practice.dto.ChatDetailsDto;
import com.decade.practice.dto.ChatSnapshot;
import com.decade.practice.dto.Conversation;
import com.decade.practice.dto.EventDto;
import com.decade.practice.application.exception.OutdatedVersionException;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatOrder;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import com.decade.practice.utils.EventUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service("chatService")
@RequiredArgsConstructor
public class ChatServiceImpl extends SelfAwareBean implements ChatService {

    private final UserRepository userRepo;
    private final EventService eventService;
    private final ChatRepository chatRepo;
    private final ChatOrderRepository chatOrderRepo;
    private final EventFactoryResolution factoryResolution;

    @PersistenceContext
    private EntityManager em;


    @Override
    public Chat getOrCreateChat(ChatIdentifier identifier) {
        try {
            return chatRepo.findById(identifier).orElseThrow();
        } catch (NoSuchElementException e) {
            ((ChatServiceImpl) getSelf()).ensureExists(identifier);
        }
        return chatRepo.findById(identifier).orElseThrow();
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = NoSuchElementException.class
    )
    @Override
    public void ensureExists(ChatIdentifier identifier) {
        try {
            Chat chat = new Chat(
                    userRepo.findById(identifier.getFirstUser()).orElseThrow(),
                    userRepo.findById(identifier.getSecondUser()).orElseThrow()
            );
            // transient check stuffs
            em.merge(chat);
            em.flush();
        } catch (ConstraintViolationException e) {
            log.debug("Concurrent creating chat encountered", e);
        }
    }


    @Override
    public Chat createChat(ChatIdentifier identifier) throws NoSuchElementException {
        Chat chat = new Chat(
                userRepo.findById(identifier.getFirstUser()).orElseThrow(),
                userRepo.findById(identifier.getSecondUser()).orElseThrow()
        );
        em.merge(chat);
        return chat;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public List<Chat> listChat(UUID userId, Integer version, Optional<ChatIdentifier> offset, int limit) {
        User owner = userRepo.findById(userId).orElseThrow();
        if (owner.getSyncContext().getEventVersion() != version)
            throw new OutdatedVersionException(owner.getSyncContext().getEventVersion(), version);
        Optional<ChatOrder> order = offset.flatMap(new Function<ChatIdentifier, Optional<ChatOrder>>() {
            @Override
            public Optional<ChatOrder> apply(ChatIdentifier chatIdentifier) {
                return chatOrderRepo.findByChat_IdentifierAndOwner(chatIdentifier, owner);
            }
        });

        if (order.isPresent()) {
            List<ChatOrder> chatOrders = chatOrderRepo.findByOwnerAndCurrentVersionLessThan(owner, order.get().getCurrentVersion(), PageRequest.of(0, limit, EventUtils.CURRENT_SORT_DESC));

            return chatOrders.stream().map(new Function<ChatOrder, Chat>() {
                @Override
                public Chat apply(ChatOrder chatOrder) {
                    return chatOrder.getChat();
                }
            }).toList();
        }
        List<ChatOrder> chatOrders = chatOrderRepo.findByOwnerAndCurrentVersionLessThan(owner, version + 1, PageRequest.of(0, limit, EventUtils.CURRENT_SORT_DESC));
        return chatOrders.stream().map(new Function<ChatOrder, Chat>() {
            @Override
            public Chat apply(ChatOrder chatOrder) {
                return chatOrder.getChat();
            }
        }).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("@accessPolicy.isAllowed(#chatIdentifier,#userId)")
    public ChatSnapshot getSnapshot(ChatIdentifier chatIdentifier, UUID userId, int atVersion) {
        User owner = userRepo.findById(userId).orElseThrow();
        Chat chat = chatRepo.findById(chatIdentifier).orElseThrow();
        List<EventDto> eventList = eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatIdentifier, atVersion);
        return new ChatSnapshot(
                new Conversation(chat, owner),
                eventList,
                atVersion
        );
    }

    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#chatIdentifier,#userId)")
    public ChatDetailsDto getDetails(ChatIdentifier chatIdentifier, UUID userId) {
        User owner = userRepo.findById(userId).orElseThrow();
        Chat chat = getOrCreateChat(chatIdentifier);
        return ChatDetailsDto.from(chat, owner);
    }
}
