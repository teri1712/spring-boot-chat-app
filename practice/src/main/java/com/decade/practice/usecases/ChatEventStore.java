package com.decade.practice.usecases;

import com.decade.practice.data.repositories.ChatRepository;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.ChatEvent;
import com.decade.practice.models.domain.entity.MessageEvent;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.utils.ChatUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

@Transactional(isolation = Isolation.READ_COMMITTED)
@Component
@Primary
public class ChatEventStore implements EventStore {

        private final UserEventStore userEventStore;
        private final ChatRepository chatRepo;
        private final ChatOperations chatOperations;

        public ChatEventStore(UserEventStore userEventStore, ChatRepository chatRepo, ChatOperations chatOperations) {
                this.userEventStore = userEventStore;
                this.chatRepo = chatRepo;
                this.chatOperations = chatOperations;
        }

        @Override
        public Collection<ChatEvent> save(ChatEvent event) throws NoSuchElementException, ConstraintViolationException {
                // Ensure chat exists before locking/updating
                Chat chat = chatOperations.getOrCreateChat(event.getChatIdentifier());
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
                result.addAll(userEventStore.save(mine));
                result.addAll(userEventStore.save(yours));
                return result;
        }
}
