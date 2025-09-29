package com.decade.practice.application.services;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.MessageEvent;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.ChatRepository;
import com.decade.practice.utils.ChatUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

@Transactional(isolation = Isolation.READ_COMMITTED)
@Service
@Primary
public class ChatEventStore implements EventStore {

        private final EventStore userEventStore;
        private final ChatRepository chatRepo;
        private final ChatService chatService;

        public ChatEventStore(EventStore userEventStore, ChatRepository chatRepo, ChatService chatService) {
                this.userEventStore = userEventStore;
                this.chatRepo = chatRepo;
                this.chatService = chatService;
        }

        @Override
        public Collection<ChatEvent> save(ChatEvent event) throws NoSuchElementException, ConstraintViolationException {
                Chat chat = chatRepo.findByIdWithPessimisticLock(event.getChatIdentifier());
                if (chat == null) {
                        chatService.createChat(event.getChatIdentifier());
                        chat = chatRepo.findByIdWithPessimisticLock(event.getChatIdentifier());
                }
                if (event instanceof MessageEvent) {
                        chat.setMessageCount(chat.getMessageCount() + 1);
                }
                event.setChat(chat);

                User me = ChatUtils.inspectOwner(chat, event.getSender());
                User you = ChatUtils.inspectPartner(chat, me);

                ChatEvent mine = event.copy();
                mine.setReceipt(event.getReceipt());

                ChatEvent yours = event.copy();

                mine.setOwner(me);
                yours.setOwner(you);

                Collection<ChatEvent> result = new ArrayList<>();
                result.addAll(userEventStore.save(mine));
                result.addAll(userEventStore.save(yours));
                return result;
        }
}
