package com.decade.practice.usecases;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.MessageEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.utils.ChatUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

@Component
@Primary
public class ChatEventStore implements EventStore {

        private final UserEventStore eventStore;
        private final ChatOperations chatOperations;

        @PersistenceContext
        private EntityManager em;

        public ChatEventStore(UserEventStore eventStore, ChatOperations chatOperations) {
                this.eventStore = eventStore;
                this.chatOperations = chatOperations;
        }

        @Transactional(isolation = Isolation.READ_COMMITTED)
        @Override
        public Collection<ChatEvent> save(ChatEvent event) throws NoSuchElementException, ConstraintViolationException {
                Chat chat = chatOperations.getOrCreateChat(event.getChatIdentifier());
                em.lock(chat, LockModeType.PESSIMISTIC_WRITE);
                if (event instanceof MessageEvent) {
                        chat.setMessageCount(chat.getMessageCount() + 1);
                }
                event.setChat(chat);

                User me = ChatUtils.inspectOwner(chat, event.getSender());
                User you = ChatUtils.inspectPartner(chat, me);

                ChatEvent mine = event.copy();
                mine.setLocalId(event.getLocalId());

                ChatEvent yours = event.copy();

                mine.setOwner(me);
                yours.setOwner(you);

                Collection<ChatEvent> result = new ArrayList<>();
                result.addAll(eventStore.save(mine));
                result.addAll(eventStore.save(yours));
                return result;
        }
}
