package com.decade.practice.application.services;

import com.decade.practice.application.domain.GroupChatIdentifierMaker;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.CreateChatCommandHandler;
import com.decade.practice.dto.ChatDetails;
import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class GroupCreateChatCommandHandler implements CreateChatCommandHandler {

    private final GroupChatIdentifierMaker identifierMaker;
    private final ChatService chatService;
    private final UserRepository userRepository;

    @Override
    public ChatDetails create(UUID requesterId, UUID partnerId) {
        User user = userRepository.findById(requesterId).orElseThrow();
        User partner = userRepository.findById(partnerId).orElseThrow();
        String chatId = identifierMaker.make(new ChatCreators(user, partner));
        String roomName = user.getName() + "'s room";
        try {
            return chatService.createChat(chatId, requesterId, roomName, 1, partner.getId());
        } catch (DataIntegrityViolationException e) {
            log.debug("Concurrent chat creation encountered", chatId);
            return chatService.getDetails(chatId, requesterId);
        }

    }
}
