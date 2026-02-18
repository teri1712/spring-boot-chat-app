package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ChatCreated;
import com.decade.practice.threads.application.ports.out.ChatHistoryRepository;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.ChatHistory;
import com.decade.practice.threads.domain.ChatHistoryId;
import com.decade.practice.threads.domain.Message;
import com.decade.practice.threads.domain.MessageEvent;
import com.decade.practice.threads.domain.events.MessageReady;
import com.decade.practice.threads.domain.events.PreferenceReady;
import com.decade.practice.threads.domain.events.SeenReady;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatHistoryManagement {

    protected final ChatHistoryRepository chatHistories;
    private final EventRepository events;

    @ApplicationModuleListener
    public void on(ChatCreated event) {
        String chatId = event.chatId();
        event.participants().forEach(participant -> {

            ChatHistoryId historyId = new ChatHistoryId(chatId, participant);
            ChatHistory history = chatHistories.findById(historyId)
                    .orElseGet(() -> new ChatHistory(chatId, participant, event.roomName(), event.roomAvatar()));
            chatHistories.save(history);
        });
    }


    @EventListener
    public void on(PreferenceReady prefs) {
        ChatHistoryId historyId = new ChatHistoryId(prefs.chatId(), prefs.ownerId());
        ChatHistory history = chatHistories.findById(historyId).orElseThrow();
        history.update(prefs.roomName(), prefs.roomAvatar());
        chatHistories.save(history);
    }

    @EventListener
    public void on(SeenReady seenReady) {
        ChatHistoryId historyId = new ChatHistoryId(seenReady.chatId(), seenReady.ownerId());
        ChatHistory history = chatHistories.findById(historyId).orElseThrow();
        history.addSeenBy(seenReady.sender());
        chatHistories.save(history);
    }

    @EventListener
    public void on(MessageReady messageReady) {
        MessageEvent event = (MessageEvent) events.findById(messageReady.id()).orElseThrow();

        String chatId = messageReady.chatId();
        ChatHistoryId historyId = new ChatHistoryId(chatId, messageReady.ownerId());

        ChatHistory history = chatHistories.findById(historyId)
                .orElseGet(() -> new ChatHistory(chatId, messageReady.ownerId(), null, null));

        Message message = new Message(messageReady.senderId(), messageReady.message(), messageReady.createdAt());
        history.addMessage(message);

        chatHistories.save(history);
    }
}
