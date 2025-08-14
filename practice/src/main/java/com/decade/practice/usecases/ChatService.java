package com.decade.practice.usecases;

import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.data.repositories.*;
import com.decade.practice.model.domain.ChatSnapshot;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.Edge;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.local.Conversation;
import com.decade.practice.utils.EventUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatService extends SelfAwareBean implements ChatOperations {

        private final UserRepository userRepo;
        private final EventRepository eventRepo;
        private final EdgeRepository edgeRepo;
        private final ChatRepository chatRepo;

        @PersistenceContext
        private EntityManager em;

        public ChatService(
                UserRepository userRepo,
                EventRepository eventRepo,
                EdgeRepository edgeRepo,
                ChatRepository chatRepo
        ) {
                this.userRepo = userRepo;
                this.eventRepo = eventRepo;
                this.edgeRepo = edgeRepo;
                this.chatRepo = chatRepo;
        }

        @Override
        public Chat getOrCreateChat(ChatIdentifier identifier) {
                try {
                        return EntityHelper.get(chatRepo, identifier);
                } catch (NoSuchElementException e) {
                        e.printStackTrace();
                        ensureExists(identifier);
                }
                return EntityHelper.get(chatRepo, identifier);
        }

        private void ensureExists(ChatIdentifier chatIdentifier) {
                try {
                        ((ChatService) getSelf()).createChat(chatIdentifier);
                } catch (ConstraintViolationException ignored) {
                        ignored.printStackTrace();
                }
        }

        @Transactional(
                propagation = Propagation.REQUIRES_NEW,
                noRollbackFor = NoSuchElementException.class
        )
        public void createChat(ChatIdentifier identifier) throws NoSuchElementException {
                Chat chat = new Chat(
                        EntityHelper.get(userRepo, identifier.getFirstUser()),
                        EntityHelper.get(userRepo, identifier.getSecondUser())
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
                        (edgeRepo.getHeadEdge(owner, effectiveVersion) != null ?
                                edgeRepo.getHeadEdge(owner, effectiveVersion).getFrom() : null);

                if (currentChat == null) {
                        return new ArrayList<>();
                }

                int count = limit;
                List<Chat> chatList = new ArrayList<>();
                chatList.add(currentChat);

                while (--count >= 0) {
                        Edge edge = edgeRepo.getEdgeFrom(owner, currentChat, effectiveVersion);
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
                PageRequest page = EventUtils.EVENT_VERSION_LESS_THAN_EQUAL;
                List<ChatEvent> eventList = eventRepo.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, atVersion, page);
                return new ChatSnapshot(
                        new Conversation(chat, owner),
                        eventList,
                        atVersion
                );
        }
}
