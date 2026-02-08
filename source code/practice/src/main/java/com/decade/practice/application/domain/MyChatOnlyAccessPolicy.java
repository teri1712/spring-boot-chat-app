package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("accessPolicy")
@AllArgsConstructor
public class MyChatOnlyAccessPolicy implements AccessPolicy {
    private final ChatRepository chatRepository;

    @Override
    public boolean isAllowed(String chatId, UUID userId) {
        return chatRepository.existsByIdentifierAndParticipants_Id(chatId, userId);
    }
}
