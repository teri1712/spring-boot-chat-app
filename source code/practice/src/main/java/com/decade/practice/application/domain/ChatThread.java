package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.ChatOrder;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ChatThread {
    private final ChatOrderRepository chatOrderRepository;
    private final MessagePolicy messagePolicy;

    public void bubbleUp(ChatOrder chatOrder, ChatEvent withMessage) {
        if (!messagePolicy.isMessage(withMessage)) {
            return;
        }
        User owner = chatOrder.getOwner();
        chatOrder.setCurrentEvent(withMessage);
        chatOrder.setCurrentVersion(withMessage.getEventVersion());
        chatOrderRepository.save(chatOrder);
    }
}
