package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Message;
import com.decade.practice.inbox.domain.events.SeenRoomEventCreated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class SeenListener {

    private final MessageRepository messages;

    @ApplicationModuleListener(id = "seen_listener")
    public void on(SeenRoomEventCreated event) {
        String chatId = event.getChatId();
        UUID senderId = event.getSenderId();
        Optional<Message> latestSeen = messages.findByLastSeen(chatId, senderId);
        Optional<Message> latestMessage = messages.findFirstByChatIdOrderBySequenceIdDesc(chatId);

        if (latestSeen.equals(latestMessage)) {
            return;
        }

        latestSeen.ifPresent(new Consumer<Message>() {

            @Override
            public void accept(Message message) {
                message.deleteSeen(senderId);
                log.debug("Removing seen pointer for user {} in chat {} for message {}", senderId, chatId, message.getSequenceId());
                messages.save(message);
            }
        });

        latestMessage.ifPresent(new Consumer<Message>() {
            @Override
            public void accept(Message message) {
                message.addSeen(senderId, event.getAt());
                log.debug("Adding seen pointer for user {} in chat {} for message {}", senderId, chatId, message.getSequenceId());
                messages.save(message);
            }
        });
    }

}
