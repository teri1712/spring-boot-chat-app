package com.decade.practice.application.services;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.domain.ChatSnapshot;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.Edge;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.locals.Conversation;
import com.decade.practice.domain.repositories.ChatRepository;
import com.decade.practice.domain.repositories.EdgeRepository;
import com.decade.practice.domain.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatServiceImpl extends SelfAwareBean implements ChatService {

        private final UserRepository userRepo;
        private final EventService eventService;
        private final EdgeRepository edgeRepo;
        private final ChatRepository chatRepo;

        @PersistenceContext
        private EntityManager em;

        public ChatServiceImpl(
                UserRepository userRepo,
                EventService eventService,
                EdgeRepository edgeRepo,
                ChatRepository chatRepo
        ) {
                this.userRepo = userRepo;
                this.eventService = eventService;
                this.edgeRepo = edgeRepo;
                this.chatRepo = chatRepo;
        }

        @Override
        public Chat getOrCreateChat(ChatIdentifier identifier) {
                try {
                        return chatRepo.findById(identifier).get();
                } catch (NoSuchElementException e) {
//                        e.printStackTrace();
                        ensureExists(identifier);
                }
                return chatRepo.findById(identifier).get();
        }

        private void ensureExists(ChatIdentifier chatIdentifier) {
                try {
                        ((ChatServiceImpl) getSelf()).createChat(chatIdentifier);
                } catch (ConstraintViolationException ignored) {
                        ignored.printStackTrace();
                }
        }

        @Transactional(
                propagation = Propagation.REQUIRES_NEW,
                noRollbackFor = NoSuchElementException.class
        )
        @Override
        public void createChat(ChatIdentifier identifier) throws NoSuchElementException {
                Chat chat = new Chat(
                        userRepo.findById(identifier.getFirstUser()).get(),
                        userRepo.findById(identifier.getSecondUser()).get()
                );
                em.persist(chat);
                em.flush();
        }

        @Transactional(isolation = Isolation.READ_COMMITTED)
        @Override
        public List<Chat> listChat(
                User owner,
                Integer version,
                Chat offset,
                int limit
        ) {
                int effectiveVersion = version != null ? version : owner.getSyncContext().getEventVersion();
                Chat currentChat = offset != null ? offset :
                        (edgeRepo.findHeadEdge(owner, effectiveVersion) != null ?
                                edgeRepo.findHeadEdge(owner, effectiveVersion).getFrom() : null);

                if (currentChat == null) {
                        return new ArrayList<>();
                }

                int count = limit;
                List<Chat> chatList = new ArrayList<>();
                chatList.add(currentChat);

                while (--count >= 0) {
                        Edge edge = edgeRepo.findEdgeFrom(owner, currentChat, effectiveVersion);
                        if (edge != null && edge.getDest() != null) {
                                currentChat = edge.getDest();
                                chatList.add(currentChat);
                        } else {
                                break;
                        }
                }

                return chatList;
        }

        @Override
        public ChatSnapshot getSnapshot(
                Chat chat,
                User owner,
                int atVersion
        ) {
                List<ChatEvent> eventList = eventService.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, atVersion);
                return new ChatSnapshot(
                        new Conversation(chat, owner),
                        eventList,
                        atVersion
                );
        }
}
